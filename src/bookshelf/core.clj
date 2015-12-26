(ns bookshelf.core
  (:gen-class)
  (:use [clojure.string :as str])
  (:use [ring.adapter.jetty :as jetty])
  (:use [ring.middleware.reload :as reload])
  (:use [ring.middleware.stacktrace :as stacktrace])
  (:use [compojure.core :refer :all])
  (:use [compojure.route :as route])
  (:use [hiccup.core :as hiccup]))

(def library-dir "/Users/dan/GoogleDrive/Literature")

(defn is-book [file]
  (let [path (.getPath file)]
    (or (.endsWith path ".pdf")
        (.endsWith path ".epub"))))

(defn library-files []
  (filter #'is-book (file-seq (clojure.java.io/file library-dir))))

(defn library-files-html []
  (hiccup/html
   [:ul
    (for [file (library-files)]
      [:li (.getPath file)])]))

(defroutes handler
  (GET "/" [] (library-files-html))
  (route/not-found "Invalid request"))

(def app
  (-> #'handler
      (reload/wrap-reload '(bookshelf.core))
      (stacktrace/wrap-stacktrace)))

(defn run []
  (jetty/run-jetty #'app {:port 3000}))
