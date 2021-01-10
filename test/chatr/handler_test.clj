(ns chatr.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [chatr.handler :refer [chatr]]))

(deftest test-app
  (testing "main route"
    (let [response (chatr (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "<<chatr>> Speak Freely <<chatr>>"))))

  (testing "not-found route"
    (let [response (chatr (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
