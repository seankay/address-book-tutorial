(ns address-book.core.routes.address-book-routes
  (:require [compojure.core :refer :all]
            [ring.util.response :as response]
            [address-book.core.views.address-book-layout :refer [common-layout
                                                                 add-contact-form
                                                                 edit-contact
                                                                 read-contact]]
            [address-book.core.models.database :refer [db]]
            [address-book.core.models.query-defs :as query]))

(defn display-contact [contact contact-id]
  (if (not= (and contact-id (Integer. contact-id)) (:id contact))
    (read-contact contact)
    (edit-contact contact)))

(defn delete-route [request]
  (let [contact-id (get-in request [:params :contact-id])]
    (query/delete-contact<! { :id (Integer. contact-id)} {:connection db})
    (response/redirect "/")))

(defn update-route [request]
  (let [contact-id (get-in request [:params :id])
        name (get-in request [:params :name])
        phone (get-in request [:params :phone])
        email (get-in request [:params :email])]
    (query/update-contact<! {:name name :email email :phone phone :id (Integer. contact-id)}
                             {:connection db})
    (response/redirect "/")))

(defn post-route [request]
  (let [name (get-in request [:params :name])
        phone (get-in request [:params :phone])
        email (get-in request [:params :email])]
    (query/insert-contact<! { :name name :phone phone :email email } {:connection db})
    (response/redirect "/")))

(defn get-route [request]
  (let [contact-id (get-in request [:params :contact-id])]
  (common-layout
    (for [contact (query/all-contacts {} {:connection db})]
      (display-contact contact contact-id))
    (add-contact-form))))

(defn example-get [request]
  (common-layout
    [:p "Example GET"]))

(defroutes address-book-routes
  (GET "/" [] get-route)
  (POST "/delete/:contact-id" [] delete-route)
  (GET "/edit/:contact-id" [] get-route)
  (POST "/edit/:contact-id" [] update-route)
  (POST "/post" [] post-route))
