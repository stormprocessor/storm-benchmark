(defproject storm/storm-benchmark "0.0.1-SNAPSHOT"
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :test-paths ["test/clj"]
  :resource-paths ["./conf"]
  :javac-options {:debug "true"}
  :dependencies [[storm/storm-core "0.9.0-wip19"]
                 [storm/storm-netty "0.9.0-wip19"]
                 [org.clojure/clojure "1.4.0"]]
  :jvm-opts ["-Djava.library.path=/usr/local/lib:/opt/local/lib:/usr/lib"])
