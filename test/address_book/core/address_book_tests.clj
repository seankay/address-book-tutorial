(ns address-book.core.address-book-tests
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [address-book.core.handler :refer :all]
            [address-book.core.models.database :refer [database db]]
            [address-book.core.models.query-defs :as query]))

(def test-db (database :test))

(facts "Example GET and POST tests"
  (with-state-changes [(before :facts (query/create-contacts-table-if-not-exists!
                                        {} {:connection test-db})
                               :after (query/drop-contacts-table!
                                        {} {:connection test-db}))]
  (fact "Test GET"
    (with-redefs [db test-db]
      (query/insert-contact<! {:name "Sean Kay"
                                :phone "(321) 222-2222"
                                :email "test@example.com"}
                            {:connection test-db})
      (query/insert-contact<! {:name "Test"
                                :phone "(555) 222-2222"
                                :email "test1@example.com"}
                            {:connection test-db})
      (let [response (app (mock/request :get "/"))]
        (:status response) => 200
        (:body response) => (contains "<div class=\"column-1\">Sean Kay</div>"))))

  (fact "Test POST"
    (with-redefs [db test-db]
      (count (query/all-contacts {} {:connection test-db})) => 0

      (let [response (app (mock/request :post
                                        "/post"
                                        {:name "John Smith"
                                         :phone "(555) 253-3333"
                                         :email "test@example.com"}))]
        (:status response) => 302
        (count (query/all-contacts {} {:connection test-db})) => 1)))))
