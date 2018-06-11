(defproject io.pedestal/pedestal.interceptor "0.5.4-SNAPSHOT"
  :description "Pedestal interceptor chain and execution utilities"
  :url "https://github.com/pedestal/pedestal"
  :scm "https://github.com/pedestal/pedestal"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.474" :exclusions [org.clojure/tools.analyzer.jvm]]
                 [io.pedestal/pedestal.log "0.5.4-SNAPSHOT"]

                 ;; Error interceptor tooling
                 [org.clojure/core.match "0.3.0-alpha5" :exclusions [[org.clojure/clojurescript]
                                                                     [org.clojure/tools.analyzer.jvm]]]
                 [org.clojure/tools.analyzer.jvm "0.7.2"]]
  :min-lein-version "2.0.0"
  :pedantic? :abort
  :global-vars {*warn-on-reflection* true}

  :aliases {"docs" ["with-profile" "docs" "codox"]}

  :profiles {:docs {:pedantic? :ranges
                    :plugins [[lein-codox "0.9.5"]]}})
