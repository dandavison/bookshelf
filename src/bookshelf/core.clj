(ns bookshelf.core
  (:gen-class)
  (:use [clojure.string :as str])
  (:use [ring.adapter.jetty :as jetty])
  (:use [ring.middleware.reload :as reload])
  (:use [ring.middleware.stacktrace :as stacktrace])
  (:use [compojure.core :refer :all])
  (:use [compojure.route :as route])
  (:use [hiccup.page :as hiccup])
  (:use [clojure.java.jdbc :as sql]))

(def library-dir "/Users/dan/GoogleDrive/Literature")

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(defn is-book [file]
  (let [path (.getPath file)]
    (and
     (.isFile file)
     (or (.endsWith path ".pdf")
         (.endsWith path ".epub")))))

(defn library-files []
  (filter #'is-book (file-seq (clojure.java.io/file library-dir))))

(defn create-db []
  (try (sql/db-do-commands
        db
        (sql/create-table-ddl :books
                          [:name :text]
                          [:path :text]
                          [:hash :text]))
       (catch Exception e (println e))))

(defn populate-db []
  (for [file (library-files)]
    (sql/insert! db :books
                 {:name "name"
                  :path (.getPath file)
                  :hash "hash"})))

(defn library-files-html []
  (hiccup/html5
   [:head
    [:title "bookshelf"]
    (hiccup/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")]
   [:table.table.table-hover.table-bordered
    [:thead
     [:tr [:td "Path"]]]
    [:tbody
     (for [row (sql/query db "SELECT * FROM books")]
       [:tr
        [:td (:path row)]])]]))

(defroutes handler
  (GET "/" [] (library-files-html))
  (route/not-found "Invalid request"))

(def app
  (-> #'handler
      (reload/wrap-reload '(bookshelf.core))
      (stacktrace/wrap-stacktrace)))

(defn run []
  (jetty/run-jetty #'app {:port 3000}))
