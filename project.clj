(defproject bookshelf "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.xerial/sqlite-jdbc "3.8.11.2"]
                 [ring/ring-devel "1.4.0"]]
  ;; :dev-dependencies [[ring/ring-devel "1.4.0"]]
  :main ^:skip-aot bookshelf.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
