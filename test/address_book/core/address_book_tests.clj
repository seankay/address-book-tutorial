(ns address-book.core.address-book-tests
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [address-book.core.handler :refer :all]
            [address-book.core.models.database :refer [database db]]
            [address-book.core.models.query-defs :as query]))

(def test-db (database :test))

(def test-user {:name "Test User" :phone "(333) 333-3333" :email "test3@example.com"})

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
      (query/insert-contact<! test-user {:connection test-db})
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
        (count (query/all-contacts {} {:connection test-db})) => 1)))
  (fact "Test UPDATE a post request to /edit/<contat-id> updates desired contact info"
    (with-redefs [db test-db]
      (query/insert-contact<! test-user {:connection test-db})
      (let [response (app (mock/request :post "/edit/1" {:id "1" :name "Tester"
                                                         :phone "(444) 444-4444"
                                                         :email "test5@example.com"}))]
        (:status response) => 302
        (count (query/all-contacts {} {:connection test-db})) => 1
        (first (query/all-contacts {} {:connection test-db})) => {:id 1
                                                                  :name "Tester"
                                                                  :phone "(444) 444-4444"
                                                                  :email "test5@example.com"})))
  (fact "Test DELETED a post to /delete/<contact-id> deletes a desired contact from database"
    (with-redefs [db test-db]
      (query/insert-contact<! test-user {:connection test-db})
      (count (query/all-contacts {} {:connection test-db})) => 1
      (let [response (app (mock/request :post "/delete/1" {:id 1}))]
        (count (query/all-contacts {} {:connection test-db})) => 0)))))
