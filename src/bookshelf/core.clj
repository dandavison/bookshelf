(ns bookshelf.core
  (:gen-class)
  (:use [clojure.string :as str])
  (:use [ring.adapter.jetty :as jetty])
  (:use [ring.middleware.json :as json])
  (:use [ring.middleware.reload :as reload])
  (:use [ring.middleware.resource])
  (:use [ring.middleware.stacktrace :as stacktrace])
  (:use [ring.util.response :refer [response resource-response]])
  (:use [compojure.core :as compojure])
  (:use [compojure.route :as route])
  (:use [hiccup.page :as hiccup])
  (:use [clojure.java.jdbc :as sql])
  (:use [pandect.algo.md5 :as md5])
  (:use [clojure.contrib.humanize :as humanize]))

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
  (sql/db-do-commands
   db
   (sql/drop-table-ddl :books)
   (sql/create-table-ddl :books
                         [:name :text]
                         [:hash :text]
                         [:size :float]
                         [:path :text])))

(defn populate-db []
  (for [file (library-files)]
    (sql/insert! db :books
                 {:name (.getName file)
                  :hash (md5/md5-file file)
                  :size (.length file)
                  :path (.getPath file)})))

(defn make-notes [row counts]
  (let [count (get counts (:hash row))]
    (if (> count 1) (format "%d copies" count))))

(defn library-files-html []
  (let [rows (sql/query db "SELECT * FROM books ORDER BY name")
        counts (frequencies (map :hash rows))]
    (hiccup/html5
     [:head
      [:title "bookshelf"]
      (hiccup/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")]
     [:table.table.table-hover.table-bordered
      [:thead
       [:tr
        [:td "Name"]
        [:td "Size"]
        [:td "Notes"]]]
      [:tbody
       (for [row rows]
         [:tr
          [:td (:name row)]
          [:td (humanize/filesize (:size row))]
          [:td (make-notes row counts)]])]])))

(defn library-files-data []
  (for [row (sql/query db "SELECT * FROM books ORDER BY name")]
    {:name (:name row)
     :hash (:hash row)
     :size (:size row)
     :path (:path row)}))

(defroutes handler
  (GET "/hiccup/" [] (library-files-html))
  (GET "/json/" [] (response (library-files-data)))
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (not-found "Invalid request"))

(def app
  (-> #'handler
      (wrap-resource "public")
      (json/wrap-json-body)
      (json/wrap-json-response)
      (reload/wrap-reload '(bookshelf.core))
      (stacktrace/wrap-stacktrace)))

(defn run []
  (jetty/run-jetty #'app {:port 3000}))
