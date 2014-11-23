(ns address-book.core.routes.address-book-routes
  (:require [compojure.core :refer :all]
            [ring.util.response :as response]
            [address-book.core.views.address-book-layout :refer [common-layout
                                                                 add-contact-form
                                                                 read-contact]]
            [address-book.core.models.database :refer [db]]
            [address-book.core.models.query-defs :as query]))

(defn post-route [request]
  (let [name (get-in request [:params :name])
        phone (get-in request [:params :phone])
        email (get-in request [:params :email])]
    (query/insert-contact<! { :name name :phone phone :email email } {:connection db})
    (response/redirect "/")))

(defn get-route [request]
  (common-layout
    (for [contact (query/all-contacts {} {:connection db})]
      (read-contact contact))
    (add-contact-form)))

(defn example-get [request]
  (common-layout
    [:p "Example GET"]))

(defroutes address-book-routes
  (GET "/" [] get-route)
  (POST "/post" [] post-route))
