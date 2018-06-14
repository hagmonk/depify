([org.clojure/clojure "1.9.0" :scope "provided"]
 [org.clojure/clojurescript "1.10.238" :scope "provided"]
 [binaryage/env-config "0.2.2"]
 [org.clojure/tools.nrepl "0.2.12" :exclusions ([org.clojure/clojure])]
 [clojure-complete/clojure-complete
  "0.2.4"
  :exclusions
  ([org.clojure/clojure])])
([lein-cljsbuild/lein-cljsbuild "1.1.6"]
 [lein-shell/lein-shell "0.5.0"]
 [lein-ancient/lein-ancient "0.6.15"]
 [lein-cljfmt/lein-cljfmt "0.5.7"]
 [lein-libdir/lein-libdir "0.1.1"]
 [lein-pprint/lein-pprint "1.2.0"]
 [com.jakemccrary/lein-test-refresh "0.22.0"])
