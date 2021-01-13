(ns chatr.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [chatr.handler :refer [chatr]]))

(deftest test-app
  (testing "main route"
    (let [response (chatr (mock/request :get "/"))]
      (is (= (:status response) 302)))))
