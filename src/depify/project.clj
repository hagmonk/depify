(ns depify.project
  (:require
   [clojure.pprint :as pprint]
   [clojure.zip :as z]))

(defn form-seq
  [^java.io.BufferedReader rdr]
  (when-let [form (read {:eof nil} rdr)]
    (cons form (lazy-seq (form-seq rdr)))))

(defn get-project-clj [path]
  (with-open [r (java.io.PushbackReader.
                 (clojure.java.io/reader path))]
    (binding [*read-eval* false]
      (doall (form-seq r)))))

(defn get-form-path
  [forms & path]
  (let [node   (z/seq-zip forms)
        search (reduce
                (fn [node pred]
                  (loop [n node]
                    (cond
                      (and (fn? pred) (pred (z/node n)))         n
                      (and (not (fn? pred)) (= (z/node n) pred)) n
                      (z/end? n)                                 (reduced nil)
                      :else                                      (recur (z/next n)))))
                node
                path)]
    (some-> search z/node)))

(defn get-deps-edn
  [path]
  (if (.exists (clojure.java.io/as-file path))
    (clojure.edn/read-string (slurp path))
    {}))

(defmulti lein-key->deps (fn [_ x _] x))

(defmethod lein-key->deps :dependencies
  [deps _ form]
  (let [dependencies (get-form-path form 'defproject :dependencies vector?)
        deps-map (reduce
                  (fn [m [path version & extra]]
                    (let [coord {:mvn/version version}]
                      (-> m
                          (assoc path coord)
                          (cond->
                            (= :exclusions (first extra))
                            (update path assoc :exclusions (second extra))

                            (and (seq? version)
                                 (symbol? (second version)))
                            (update path assoc :mvn/version (get-form-path form 'def (second version) string?))))))
                  {}
                  dependencies)]
    (assoc deps :deps deps-map)))

(defmethod lein-key->deps :repositories
  [deps _ form]
  (let [repos (get-form-path form 'defproject :repositories vector?)]
    (if repos
      (assoc deps :mvn/repos (into {} repos))
      deps)))

(defmethod lein-key->deps :jvm-opts
  [deps _ form]
  (let [jvm-opts (get-form-path form 'defproject :jvm-opts vector?)]
    (if jvm-opts
      (assoc deps :jvm-opts jvm-opts)
      deps)))

(defn pprint-write [m out-file]
  (with-open [w (clojure.java.io/writer out-file)]
    (binding [*out* w]
      (pprint/pprint m))))

(let [proj (get-project-clj "project.clj")]
    (-> "deps.edn"
        get-deps-edn
        (lein-key->deps :repositories proj)
        (lein-key->deps :dependencies proj)
        (lein-key->deps :jvm-opts proj)
        (pprint-write "deps.edn")))
