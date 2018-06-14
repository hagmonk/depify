(ns depify.project-test
  (:require [depify.project :refer :all]
            [clojure.test :as t :refer [deftest is testing]]
            [clojure.spec.alpha :as s]
            [clojure.tools.deps.alpha :as dta]
            [clojure.tools.deps.alpha.specs :as dtas]))

(def sample '((def something "0.1.2")
              (def unused "unused")
              (defproject foobar "1.0.0"
                :repositories [["dracular" "https//transy.edu"]]
                :dependencies [[something/gizmo ~something]
                               [org.apache/another-database "1.2.3" :exclusions [logging-nightmare]]]
                :jvm-opts ["-XX:+EnormousBiceps"]
                :source-paths ["src"]
                :main super.dooper
                :resource-paths ["resources"]
                :profiles {:dev {:jvm-opts ^:replace ["-XX123"]}})))

(deftest inline-sample-test
  (testing "sample with symbol resolution"
    (is (->> sample
             (read-prj {})
             process
             pprint-deps
             (s/valid? ::dtas/deps-map)))))


(deftest leiningen-sample-project-test
  (let [proj (get-project-clj "test/depify/sample_project.clj")]
    (is (->> proj
             (read-prj {})
             process
             pprint-deps
             (s/valid? ::dtas/deps-map)))))

(deftest pedestal-project-test
  (let [proj (get-project-clj "test/depify/pedestal_project.clj")]
    (is (->> proj
             (read-prj {})
             process
             pprint-deps
             (s/valid? ::dtas/deps-map)))))

(defn compare-helper
  ([form answer] (compare-helper form answer {}))
  ([form answer default-deps]
   (binding [default-deps-template default-deps]
     (let [result (->> form
                       (read-prj {})
                       process
                       pprint-deps)]
       
       (is (= result answer))
       (is (s/valid? ::dtas/deps-map result))))))

(deftest readme-demo-existing-deps-test
  (let [form '((defproject super-dooper "1.0"
                 :dependencies [[tick/tock "1.0"]]))
        answer '{:deps {ding/dong {:mvn/version "1.0"}, tick/tock {:mvn/version "1.0"}}}]
    (binding [default-deps-template {}]
      (let [result (->> form
                        (read-prj '{:deps {ding/dong {:mvn/version "1.0"}}})
                        process
                        pprint-deps)]
        
        (is (= result answer))
        (is (s/valid? ::dtas/deps-map result))))))

(deftest readme-demo-vars-test
  (let [form '((def something "0.1.2")
               (defproject foobar "1.0"
                 :dependencies [[something/gizmo ~something]]))
        answer '{:deps {something/gizmo {:mvn/version "0.1.2"}}}]
    (compare-helper form answer)))

(deftest readme-demo-run-test
  (let [form   '((defproject super-dooper "1.0"
                   :jvm-opts ["-XX:+EnormousBiceps"]
                   :main super-dooper.core
                   :dependencies [[ding/dong "1.0"]]))
        answer '{:aliases
                 {:run
                  {:jvm-opts  ["-XX:+EnormousBiceps"],
                   :main-opts ["-m" "super-dooper.core"]}},
                 :deps {ding/dong {:mvn/version "1.0"}}}]
    (compare-helper form answer)))

(deftest readme-demo-profiles-test
  (let [form '((defproject super-dooper "1.0"
                 :jvm-opts ["-XX:+EnormousBiceps"]
                 :main super-dooper.core
                 :dependencies [[ding/dong "1.0"]]
                 :profiles {:dev {:dependencies [[tick/tock "1.0"]]
                                  :jvm-opts ["-XX:+CrashAndBurn"]}}))
        answer '{:aliases
                 {:run
                  {:jvm-opts ["-XX:+EnormousBiceps"],
                   :main-opts ["-m" "super-dooper.core"]},
                  :dev
                  {:jvm-opts ["-XX:+CrashAndBurn"],
                   :extra-deps {tick/tock {:mvn/version "1.0"}}}},
                 :deps {ding/dong {:mvn/version "1.0"}}} ]
    (compare-helper form answer)))

(deftest readme-demo-test-paths-test
  (let [form '((defproject super-dooper "1.0"
                 :dependencies [[ding/dong "1.0"]]
                 :test-paths ["testomatic"]))
        answer '{:aliases
                 {:test
                  {:extra-paths ["test" "testomatic"],
                   :extra-deps {org.clojure/test.check {:mvn/version "RELEASE"}}},
                  :runner
                  {:extra-deps
                   {com.cognitect/test-runner
                    {:git/url "https://github.com/cognitect-labs/test-runner",
                     :sha "76568540e7f40268ad2b646110f237a60295fa3c"}},
                   :main-opts ["-m" "cognitect.test-runner" "-d" "test"]}},
                 :deps {ding/dong {:mvn/version "1.0"}}}]
    (compare-helper form answer default-deps-template)))

(deftest readme-demo-source-resource-paths-test
  (let [form '((defproject super-dooper "1.0"
                 :dependencies [[ding/dong "1.0"]]
                 :resource-paths ["more-resources"]
                 :source-paths ["more-sources"]))
        
        answer '{:deps  {ding/dong {:mvn/version "1.0"}},
                 :paths ["src" "more-sources" "more-resources"]}]
    (compare-helper form answer)))

(deftest readme-demo-respositories-test
  (let [form   '((defproject super-dooper "1.0"
                   :dependencies [[ding/dong "1.0"]]
                   :repositories [["java.net" "https://download.java.net/maven/2"]
                                  ["releases"
                                   {:url      "https://blueant.com/archiva/internal"
                                    :username "milgrim",
                                    :password :env}]]))
        answer '{:deps {ding/dong {:mvn/version "1.0"}},
                 :mvn/repos
                 {"java.net" {:url "https://download.java.net/maven/2"},
                  "releases" {:url "https://blueant.com/archiva/internal"}}}]
    (compare-helper form answer)))

(deftest lein-pprint-cljs-devtools-test
  (let [form   (get-project-clj "test/depify/lein_pprint_cljs_devtools.clj")
        answer '{:aliases   {:test   {:extra-paths ["test" "/Users/test/src/other/cljs-devtools/test/src"],
                                      :extra-deps  #:org.clojure {test.check #:mvn {:version "RELEASE"}}},
                             :runner {:extra-deps #:com.cognitect {test-runner {:git/url "https://github.com/cognitect-labs/test-runner", :sha "76568540e7f40268ad2b646110f237a60295fa3c"}},
                                      :main-opts  ["-m" "cognitect.test-runner" "-d" "test"]},
                             :run    {:jvm-opts ["-XX:-OmitStackTraceInFastThrow" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"]}},
                 :deps      {org.clojure/clojure               #:mvn {:version "1.9.0"},
                             org.clojure/clojurescript         #:mvn {:version "1.10.238"},
                             binaryage/env-config              #:mvn {:version "0.2.2"},
                             org.clojure/tools.nrepl           {:mvn/version "0.2.12", :exclusions [org.clojure/clojure]},
                             clojure-complete/clojure-complete {:mvn/version "0.2.4", :exclusions [org.clojure/clojure]}},
                 :paths     ["src" "/Users/test/src/other/cljs-devtools/src/lib" "/Users/test/src/other/cljs-devtools/src/debug" "/Users/test/src/other/cljs-devtools/dev-resources" "/Users/test/src/other/cljs-devtools/test/resources" "/Users/test/src/other/cljs-devtools/scripts"],
                 :mvn/repos {"central" {:url "https://repo1.maven.org/maven2/"},
                             "clojars" {:url "https://repo.clojars.org/"}}}]
    (compare-helper form answer default-deps-template)))

(deftest lein-pprint-cljs-devtools-test
  (let [form   (get-project-clj "test/depify/lein_pprint_cljs_devtools_keys.clj")
        answer '{:deps {lein-cljfmt/lein-cljfmt           #:mvn {:version "0.5.7"},
                        org.clojure/clojure               #:mvn {:version "1.9.0"},
                        com.jakemccrary/lein-test-refresh #:mvn {:version "0.22.0"},
                        org.clojure/clojurescript         #:mvn {:version "1.10.238"},
                        lein-ancient/lein-ancient         #:mvn {:version "0.6.15"},
                        lein-shell/lein-shell             #:mvn {:version "0.5.0"},
                        lein-libdir/lein-libdir           #:mvn {:version "0.1.1"},
                        clojure-complete/clojure-complete {:mvn/version "0.2.4", :exclusions [org.clojure/clojure]},
                        binaryage/env-config              #:mvn {:version "0.2.2"},
                        lein-pprint/lein-pprint           #:mvn {:version "1.2.0"},
                        org.clojure/tools.nrepl           {:mvn/version "0.2.12", :exclusions [org.clojure/clojure]},
                        lein-cljsbuild/lein-cljsbuild     #:mvn {:version "1.1.6"}}}]
    (compare-helper form answer)))
