(ns bookshelf.core
  (:gen-class)
  (:use [ring.adapter.jetty :as jetty])
  (:use [ring.middleware.reload :as reload])
  (:use [ring.middleware.stacktrace :as stacktrace])
  (:use [compojure.core :refer :all])
  (:use [compojure.route :as route]))

(defn write-to-file []
  (with-open [w (clojure.java.io/writer "/tmp/out" :append true)]
    (.write w "hello\n"))
  "wrote to file")

(defroutes handler
  (GET "/" [] "Hello World")
  (GET "/write" [] (write-to-file))
  (route/not-found "Invalid request"))

(def app
  (-> #'handler
      (reload/wrap-reload '(bookshelf.core))
      (stacktrace/wrap-stacktrace)))

(defn run []
  (jetty/run-jetty #'app {:port 3000}))
