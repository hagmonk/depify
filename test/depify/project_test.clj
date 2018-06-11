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

(deftest readme-demo-existing-deps-test
  (let [form '((defproject super-dooper "1.0"
                 :dependencies [[tick/tock "1.0"]]))
        answer '{:deps {ding/dong {:mvn/version "1.0"}, tick/tock {:mvn/version "1.0"}}}]
    (binding [default-deps-template {}]
      (is (= (->> form
                  (read-prj '{:deps {ding/dong {:mvn/version "1.0"}}})
                  process
                  pprint-deps)
             answer))
      (is (s/valid? ::dtas/deps-map answer)))))

(deftest readme-demo-vars-test
  (let [form '((def something "0.1.2")
               (defproject foobar "1.0"
                 :dependencies [[something/gizmo ~something]]))
        answer '{:deps {something/gizmo {:mvn/version "0.1.2"}}}]
    (binding [default-deps-template {}]
      (is (= (->> form
                  (read-prj {})
                  process
                  pprint-deps)
             answer))
      (is (s/valid? ::dtas/deps-map answer)))))

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
    (binding [default-deps-template {}]
      (is (= (->> form
                  (read-prj {})
                  process
                  pprint-deps)
             answer))
      (is (s/valid? ::dtas/deps-map answer)))))

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
    (binding [default-deps-template {}]
      (is (= (->> form
                  (read-prj {})
                  process
                  pprint-deps)
             answer))
      (is (s/valid? ::dtas/deps-map answer)))))

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
    (is (= (->> form
                (read-prj {})
                process
                pprint-deps)
           answer))
    (is (s/valid? ::dtas/deps-map answer))))

(deftest readme-demo-source-resource-paths-test
  (let [form '((defproject super-dooper "1.0"
                 :dependencies [[ding/dong "1.0"]]
                 :resource-paths ["more-resources"]
                 :source-paths ["more-sources"]))
        
        answer '{:deps  {ding/dong {:mvn/version "1.0"}},
                 :paths ["src" "more-sources" "more-resources"]}]
    (binding [default-deps-template {}]
      (is (= (->> form
                  (read-prj {})
                  process
                  pprint-deps)
             answer))
      (is (s/valid? ::dtas/deps-map answer)))))

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
    (binding [default-deps-template {}]
      (is (= (->> form
                  (read-prj {})
                  process
                  pprint-deps)
             answer))
      (is (s/valid? ::dtas/deps-map answer)))))
