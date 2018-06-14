{:description
 "A collection of Chrome DevTools enhancements for ClojureScript developers.",
 :compile-path "/Users/test/src/other/cljs-devtools/target/classes",
 :deploy-repositories
 [["clojars"
   {:url "https://clojars.org/repo/",
    :password :gpg,
    :username :gpg}]],
 :group "binaryage",
 :license
 {:name "MIT License",
  :url "http://opensource.org/licenses/MIT",
  :distribution :repo},
 :resource-paths
 ("/Users/test/src/other/cljs-devtools/dev-resources"
  "/Users/test/src/other/cljs-devtools/test/resources"
  "/Users/test/src/other/cljs-devtools/scripts"),
 :uberjar-merge-with
 {"META-INF/plexus/components.xml" leiningen.uberjar/components-merger,
  "data_readers.clj" leiningen.uberjar/clj-map-merger,
  #"META-INF/services/.*"
  [clojure.core/slurp
   (fn*
    [p1__3347__3349__auto__ p2__3348__3350__auto__]
    (clojure.core/str
     p1__3347__3349__auto__
     "\n"
     p2__3348__3350__auto__))
   clojure.core/spit]},
 :name "devtools",
 :checkout-deps-shares
 [:source-paths
  :test-paths
  :resource-paths
  :compile-path
  #'leiningen.core.classpath/checkout-deps-paths],
 :scm {:name "git", :url "https://github.com/binaryage/cljs-devtools"},
 :source-paths
 ("/Users/test/src/other/cljs-devtools/src/lib"
  "/Users/test/src/other/cljs-devtools/src/debug"),
 :eval-in :subprocess,
 :repositories
 [["central"
   {:url "https://repo1.maven.org/maven2/", :snapshots false}]
  ["clojars" {:url "https://repo.clojars.org/"}]],
 :test-paths ("/Users/test/src/other/cljs-devtools/test/src"),
 :cljsbuild {:builds {}},
 :target-path "/Users/test/src/other/cljs-devtools/target",
 :prep-tasks ["javac" "compile"],
 :native-path "/Users/test/src/other/cljs-devtools/target/native",
 :offline? false,
 :root "/Users/test/src/other/cljs-devtools",
 :pedantic? ranges,
 :clean-targets ["target" "test/resources/.compiled"],
 :plugins
 ([lein-cljsbuild/lein-cljsbuild "1.1.6"]
  [lein-shell/lein-shell "0.5.0"]
  [lein-ancient/lein-ancient "0.6.15"]
  [lein-cljfmt/lein-cljfmt "0.5.7"]
  [lein-libdir/lein-libdir "0.1.1"]
  [lein-pprint/lein-pprint "1.2.0"]
  [com.jakemccrary/lein-test-refresh "0.22.0"]),
 :url "https://github.com/binaryage/cljs-devtools",
 :profiles
 {:nuke-aliases
  {:aliases {}, :dependencies (), :jvm-opts nil, :eval-in nil},
  :lib
  [:nuke-aliases
   {:source-paths ["src/lib"], :resource-paths [], :test-paths []}],
  :devel
  {:cljsbuild
   {:builds
    {:devel
     {:source-paths ["src/lib" "src/debug"],
      :compiler
      {:output-to "target/devel/cljs_devtools.js",
       :output-dir "target/devel",
       :optimizations :none}}}},
   :dependencies (),
   :jvm-opts nil,
   :eval-in nil},
  :testing
  {:source-paths [],
   :resource-paths [],
   :test-paths [],
   :cljsbuild
   {:builds
    {:tests-with-config
     {:source-paths ["src/lib" "test/src/tests"],
      :compiler
      {:output-to
       "test/resources/.compiled/tests-with-config/build.js",
       :output-dir "test/resources/.compiled/tests-with-config",
       :asset-path ".compiled/tests-with-config",
       :main devtools.main,
       :optimizations :none,
       :checked-arrays :warn,
       :external-config
       {:devtools/config
        {:features-to-install [:hints],
         :fn-symbol "F",
         :print-config-overrides true}},
       :preloads [devtools.testenv devtools.preload]}},
     :dead-code
     {:source-paths ["src/lib" "test/src/dead-code"],
      :compiler
      {:output-dir "test/resources/.compiled/dead-code",
       :closure-defines {"goog.DEBUG" false},
       :optimizations :advanced,
       :output-to "test/resources/.compiled/dead-code/build.js",
       :asset-path ".compiled/dead-code",
       :checked-arrays :warn,
       :pseudo-names true,
       :external-config
       {:devtools/config {:silence-optimizations-warning true}},
       :main devtools.main}},
     :dce-no-require
     {:source-paths ["src/lib" "test/src/dead-code-no-require"],
      :compiler
      {:output-to "test/resources/.compiled/dce-no-require/build.js",
       :output-dir "test/resources/.compiled/dce-no-require",
       :asset-path ".compiled/dce-no-require",
       :main devtools.main,
       :external-config
       {:devtools/config {:silence-optimizations-warning true}},
       :optimizations :advanced,
       :checked-arrays :warn}},
     :dce-with-debug
     {:source-paths ["src/lib" "test/src/dead-code"],
      :compiler
      {:output-to "test/resources/.compiled/dce-with-debug/build.js",
       :output-dir "test/resources/.compiled/dce-with-debug",
       :asset-path ".compiled/dce-with-debug",
       :main devtools.main,
       :closure-defines {"goog.DEBUG" true},
       :external-config
       {:devtools/config {:silence-optimizations-warning true}},
       :optimizations :advanced,
       :checked-arrays :warn}},
     :tests
     {:source-paths ["src/lib" "test/src/tests"],
      :compiler
      {:output-to "test/resources/.compiled/tests/build.js",
       :output-dir "test/resources/.compiled/tests",
       :asset-path ".compiled/tests",
       :main devtools.main,
       :preloads [devtools.testenv],
       :optimizations :none,
       :checked-arrays :warn}},
     :dce-no-mention
     {:source-paths ["src/lib" "test/src/dead-code-no-mention"],
      :compiler
      {:output-to "test/resources/.compiled/dce-no-mention/build.js",
       :output-dir "test/resources/.compiled/dce-no-mention",
       :asset-path ".compiled/dce-no-mention",
       :main devtools.main,
       :external-config
       {:devtools/config {:silence-optimizations-warning true}},
       :optimizations :advanced,
       :checked-arrays :warn}},
     :dce-no-debug
     {:source-paths ["src/lib" "test/src/dead-code"],
      :compiler
      {:output-to "test/resources/.compiled/dce-no-debug/build.js",
       :output-dir "test/resources/.compiled/dce-no-debug",
       :asset-path ".compiled/dce-no-debug",
       :main devtools.main,
       :closure-defines {"goog.DEBUG" false},
       :external-config
       {:devtools/config {:silence-optimizations-warning true}},
       :optimizations :advanced,
       :checked-arrays :warn}},
     :advanced-warning
     {:source-paths ["src/lib" "test/src/advanced-warning"],
      :compiler
      {:output-to "test/resources/.compiled/advanced-warning/build.js",
       :output-dir "test/resources/.compiled/advanced-warning",
       :asset-path ".compiled/advanced-warning",
       :main devtools.main,
       :external-config
       {:devtools/config {:silence-optimizations-warning true}},
       :optimizations :advanced,
       :checked-arrays :warn}},
     :dce-no-sources
     {:source-paths ["test/src/dead-code-no-require"],
      :compiler
      {:output-to "test/resources/.compiled/dce-no-sources/build.js",
       :output-dir "test/resources/.compiled/dce-no-sources",
       :asset-path ".compiled/dce-no-sources",
       :main devtools.main,
       :external-config
       {:devtools/config {:silence-optimizations-warning true}},
       :optimizations :advanced,
       :checked-arrays :warn}}}},
   :dependencies (),
   :jvm-opts nil,
   :eval-in nil},
  :dce-pseudo-names
  {:cljsbuild
   {:builds
    {:dce-with-debug {:compiler {:pseudo-names true}},
     :dce-no-debug {:compiler {:pseudo-names true}},
     :dce-no-mention {:compiler {:pseudo-names true}},
     :dce-no-require {:compiler {:pseudo-names true}},
     :dce-no-sources {:compiler {:pseudo-names true}}}},
   :dependencies (),
   :jvm-opts nil,
   :eval-in nil},
  :auto-testing
  {:cljsbuild
   {:builds
    {:tests
     {:notify-command
      ["phantomjs"
       "test/resources/phantom.js"
       "test/resources/run-tests.html"]}}},
   :dependencies (),
   :jvm-opts nil,
   :eval-in nil},
  :adhoc-auto-testing
  {:cljsbuild
   {:builds
    {:tests
     {:notify-command
      ["phantomjs"
       "test/resources/phantom.js"
       "test/resources/run-tests-adhoc.html"]}}},
   :dependencies (),
   :jvm-opts nil,
   :eval-in nil}},
 :plugin-repositories
 [["central"
   {:url "https://repo1.maven.org/maven2/", :snapshots false}]
  ["clojars" {:url "https://repo.clojars.org/"}]],
 :aliases
 {"release"
  ["do"
   ["clean"]
   ["shell" "scripts/check-versions.sh"]
   ["shell" "scripts/prepare-jar.sh"]
   ["shell" "scripts/check-release.sh"]
   ["shell" "scripts/deploy-clojars.sh"]],
  "downgrade" "upgrade",
  "deploy" ["shell" "scripts/deploy-clojars.sh"],
  "compare-dead-code-with-pseudo-names"
  ["shell"
   "scripts/compare-dead-code.sh"
   "+testing,+dce-pseudo-names"],
  "test-dead-code"
  ["do"
   ["with-profile" "+testing" "cljsbuild" "once" "dead-code"]
   ["shell" "test/scripts/dead-code-check.sh"]],
  "auto-test"
  ["do"
   ["clean"]
   ["with-profile"
    "+testing,+auto-testing"
    "cljsbuild"
    "auto"
    "tests"]],
  "install"
  ["do"
   ["shell" "scripts/prepare-jar.sh"]
   ["shell" "scripts/local-install.sh"]],
  "test-dce-size" ["shell" "scripts/check-dce-size.sh" "+testing"],
  "test-advanced-warning"
  ["do"
   ["with-profile" "+testing" "cljsbuild" "once" "advanced-warning"]
   ["shell"
    "phantomjs"
    "test/resources/phantom.js"
    "test/resources/run-advanced-warning.html"]],
  "jar" ["shell" "scripts/prepare-jar.sh"],
  "adhoc-auto-test"
  ["do"
   ["clean"]
   ["with-profile"
    "+testing,+adhoc-auto-testing"
    "cljsbuild"
    "auto"
    "tests"]],
  "test-tests"
  ["do"
   ["with-profile" "+testing" "cljsbuild" "once" "tests"]
   ["shell"
    "phantomjs"
    "test/resources/phantom.js"
    "test/resources/run-tests.html"]],
  "test-tests-with-config"
  ["do"
   ["shell" "scripts/compile-tests-with-config.sh"]
   ["shell"
    "phantomjs"
    "test/resources/phantom.js"
    "test/resources/run-tests-with-config.html"]],
  "compare-dead-code"
  ["shell" "scripts/compare-dead-code.sh" "+testing"],
  "test"
  ["do"
   ["clean"]
   ["test-tests"]
   ["test-tests-with-config"]
   ["test-dead-code"]
   ["test-advanced-warning"]]},
 :version "0.9.10",
 :jar-exclusions [#"^\."],
 :global-vars {},
 :uberjar-exclusions [#"(?i)^META-INF/[^/]*\.(SF|RSA|DSA)$"],
 :jvm-opts
 ["-XX:-OmitStackTraceInFastThrow"
  "-XX:+TieredCompilation"
  "-XX:TieredStopAtLevel=1"],
 :dependencies
 ([org.clojure/clojure "1.9.0" :scope "provided"]
  [org.clojure/clojurescript "1.10.238" :scope "provided"]
  [binaryage/env-config "0.2.2"]
  [org.clojure/tools.nrepl
   "0.2.12"
   :exclusions
   ([org.clojure/clojure])]
  [clojure-complete/clojure-complete
   "0.2.4"
   :exclusions
   ([org.clojure/clojure])]),
 :release-tasks
 [["vcs" "assert-committed"]
  ["change" "version" "leiningen.release/bump-version" "release"]
  ["vcs" "commit"]
  ["vcs" "tag"]
  ["deploy"]
  ["change" "version" "leiningen.release/bump-version"]
  ["vcs" "commit"]
  ["vcs" "push"]],
 :test-selectors {:default (constantly true)}}
