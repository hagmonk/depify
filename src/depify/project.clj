(ns depify.project
  (:require
   [clojure.pprint :as pprint]
   [clojure.java.io :as io]
   [clojure.core.match :refer [match]])
  (:import (java.io InputStream)))

(defn form-seq
  [^java.io.BufferedReader rdr]
  (when-let [form (read {:eof nil} rdr)]
    (cons form (lazy-seq (form-seq rdr)))))

(defn stream-available? [^InputStream stream]
  (pos? (.available stream)))

(defn get-project-clj
  ([] (get-project-clj "project.clj"))
  ([path]
   (let [input (if (stream-available? System/in)
                 *in*
                 (io/as-file path))]
     (with-open [r (java.io.PushbackReader. (io/reader input))]
       (binding [*read-eval* false]
         (doall (form-seq r)))))))

(defn get-deps-edn
  [path]
  (try
    (or (clojure.edn/read-string (slurp path)) {})
    (catch Throwable ex
      {})))

(defn pprint-deps [m]
  (binding [*print-namespace-maps* false]
    (pprint/pprint m))
  m)

(defmulti handle-key (fn [ctx k v] k))

(defmethod handle-key :dependencies
  [ctx k v]
  (update ctx :deps
          (fn [existing]
            (reduce
             (fn [m [path version & extra]]
               (let [coord {:mvn/version version}
                     mvn-extra (-> (apply hash-map extra)
                                   (select-keys [:classifier :extensions :exclusions]))
                     mvn-extra (if-let [e (:exclusions mvn-extra)]
                                 (assoc mvn-extra :exclusions (mapv #(if (coll? %) (first %) %) e))
                                 mvn-extra)]
                 (assoc m (symbol path) (merge coord mvn-extra))))
             existing
             v))))

(defmethod handle-key :repositories
  [ctx k v]
  (if (seq v)
    (reduce (fn [m [rk rv]]
              (if-let [u (and (map? rv) (:url rv))]
                (assoc-in m [:mvn/repos rk :url] u)
                (assoc-in m [:mvn/repos rk :url] rv)))
            ctx
            v)
    ctx))

(defmethod handle-key :default
  [ctx k v]
  (assoc ctx k v))

(defmethod handle-key :jvm-opts
  [ctx k v]
  (if (seq v)
    (assoc-in ctx [:aliases :run :jvm-opts] v)
    ctx))

(defmethod handle-key :main
  [ctx k v]
  (if v
    (assoc-in ctx [:aliases :run :main-opts] ["-m" (str v)])
    ctx))

(defmethod handle-key :source-paths
  [ctx k v]
  (if (seq v)
    (update ctx :paths (fnil into ["src"]) v)
    ctx))

(defmethod handle-key :resource-paths
  [ctx k v]
  (if (seq v)
    (update ctx :paths (fnil into ["src"]) v)
    ctx))

(defmethod handle-key :test-paths
  [ctx k v]
  (if (seq v)
    (update-in ctx [:aliases :test :extra-paths] (fnil into []) v)
    ctx))

(defmethod handle-key :profiles
  [ctx k v]
  (reduce-kv
   (fn [m pk pv]
     (let [opts (reduce-kv handle-key {} pv)]
       (if-not (empty? opts)
         (cond-> m
           (-> opts :aliases :run)
           (update-in [:aliases pk] (fnil merge {}) (-> opts :aliases :run))
           
           (-> opts :deps)
           (update-in [:aliases pk :extra-deps] (fnil merge {}) (-> opts :deps)))
         m)))
   ctx
   v))

(defmethod handle-key :vars
  [ctx k v]
  (clojure.walk/prewalk
   #(if-let [s (and
                (seq? %)
                (symbol? (second %))
                (get v (second %)))]
      s
      %) ctx))

(defn read-prj
  [ctx forms]
     (match [forms]
            [([(['def a b] :seq)  & r] :seq)]
            (recur (assoc-in ctx [:vars a] b) r)

            [([(['defproject a v & r] :seq) & _] :seq)]
            (recur (assoc ctx :name a) r)

            [([k v & r] :seq)]
            (recur (assoc ctx k v) r)
           
            :else ctx))

(def ^:dynamic default-deps-template
  {:aliases
   {:test   {:extra-paths ["test"]
             :extra-deps  {'org.clojure/test.check {:mvn/version "RELEASE"}}}
    :runner {:extra-deps {'com.cognitect/test-runner
                          {:git/url "https://github.com/cognitect-labs/test-runner"
                           :sha     "76568540e7f40268ad2b646110f237a60295fa3c"}}
             :main-opts  ["-m" "cognitect.test-runner"
                          "-d" "test"]}}})
(defn process
  [ctx]
  (let [known-keys [:dependencies
                    :repositories
                    :jvm-opts
                    :source-paths
                    :resource-paths
                    :test-paths
                    :main
                    :profiles
                    :vars]
        result (->> known-keys
                    (reduce
                     (fn [ctx k]
                       (let [v (k ctx)]
                         (-> ctx
                             (dissoc k)
                             (handle-key k v))))
                     (merge-with merge default-deps-template (dissoc ctx :aliases))))]
    (select-keys result [:aliases :deps :paths :mvn/repos])))

(defn -main [& args]
  (when-let [proj (get-project-clj)]
    (->> proj
         (read-prj (get-deps-edn "deps.edn"))
         process
         pprint-deps)))

