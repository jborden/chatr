(ns chatr.chatroom
  (:require [ring.util.response :as response]
            [chatr.html :refer [Root]]))

(defn ChatRoom
  [request]
  (let [body (Root [:div
                    [:div {:id "app"}]
                    [:script "chatr.core.init_BANG_()"]
                    [:div {:class "ui container"}
                     [:h1 (get-in request [:cookies "chatrID" :value])]]])
        resp (response/response body)]
    (response/header resp "Content-Type" "text/html; charset=utf-8")))
