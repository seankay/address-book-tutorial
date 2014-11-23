(ns address-book.core.routes.address-book-routes
  (:require [compojure.core :refer [defroutes GET POST]]
            [address-book.core.views.address-book-layout :refer [common-layout]]))

(defn example-get [request]
  (common-layout
    [:p "Example GET"]))

(defroutes address-book-routes
  (GET "/" [] example-get)
  (POST "/post" [] example-post))

