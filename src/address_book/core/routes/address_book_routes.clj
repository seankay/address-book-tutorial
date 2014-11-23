(ns address-book.core.routes.address-book-routes
  (:require [compojure.core :refer :all]
            [ring.util.response :as response]
            [address-book.core.views.address-book-layout :refer [common-layout
                                                                 add-contact-form
                                                                 read-contact]]))

(def contacts (atom [{:id 1 :name "Sean Kay" :phone "(555) 555-5555" :email "sean@example.com"}
                     {:id 2 :name "Jarrod Taylor" :phone "(555) 666-7777" :email "Jarrod@JarrodCTaylor.com"}
                     {:id 3 :name "James Dalton"  :phone "(123) 567-8901" :email "Cooler@Roadhouse.com"}
                     {:id 4 :name "Johnny Utah"   :phone "(543) 333-1234" :email "J.Utah@Buckeyes.com"}]))

(defn next-id []
  (->>
    @contacts
    (map :id)
    (apply max)
    (+ 1)))

(defn post-route [request]
  (let [name (get-in request [:params :name])
        phone (get-in request [:params :phone])
        email (get-in request [:params :email])]
    (swap! contacts conj {:id (next-id) :name name :phone phone :email email})
    (response/redirect "/")))

(defn get-route [request]
  (common-layout
    (for [contact @contacts] (read-contact contact))
    (add-contact-form)))

(defn example-get [request]
  (common-layout
    [:p "Example GET"]))

(defroutes address-book-routes
  (GET "/" [] get-route)
  (POST "/post" [] post-route))

