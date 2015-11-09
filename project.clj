(defproject clojurehelper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [speclj "3.3.1"]
                 [com.h2database/h2 "1.4.189"]]
  :main ^:skip-aot clojurehelper.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {
                   :dependencies [[speclj "3.3.0"]]
                   }}
  :plugins [[speclj "3.3.0"]]
  :test-paths ["spec"])
