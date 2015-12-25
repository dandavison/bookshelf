(ns bookshelf.core
  (:gen-class)
  (:use ring.adapter.jetty)
  (:use ring.middleware.reload)
  (:use ring.middleware.stacktrace))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(def app
  (-> #'handler
      (wrap-reload '(bookshelf.core))
      (wrap-stacktrace)))

(defn run []
  (run-jetty #'app {:port 3000}))
