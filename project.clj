(defproject foobar
  :respositories [["dracular" "https//transy.edu"]]
  :dependencies [[something/gizmo "9.9.9"]
                 [org.apache/another-database "1.2.3" :exclusions [logging-nightmare]]]
  :jvm-opts ["-XX:+EnormousBiceps"])