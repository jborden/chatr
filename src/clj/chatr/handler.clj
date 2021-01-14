(ns chatr.handler
  (:require [buddy.auth.accessrules :refer [wrap-access-rules]]
            [buddy.core.codecs :as codecs]
            [buddy.core.nonce :as nonce]
            [chatr.html :refer [Login]]
            [chatr.chatroom :refer [ChatRoom]]
            [clojure.walk :as walk]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.util.response :as response]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def forbidden
  {:status 403
   :headers {}
   :body "Not Authorized"})

(defn authenticated? [request]
  (let [token (get-in request [:cookies "token" :value])]
    (not (nil? token))))

(defn clear-cookie []
  (-> (response/redirect "/login")
      (response/set-cookie "token" "clear" {:max-age -1})
      (response/set-cookie "chatrID" "clear" {:max-age -1})))

(def access-rules
  [{:pattern #"^/images/.*"
    :handler (constantly true)}
   {:pattern #"^/login"
    :handler (constantly true)
    :on-error (fn [_ _] forbidden)}
   {:pattern #"/favicon/favicon.ico"
    :handler (constantly true)}
   {:pattern #"^/css/.*"
    :handler (constantly true)
    :on-error (fn [_ _] forbidden)}
   {:pattern #"^/logout"
    :handler (constantly true)
    :on-error (fn [_ _] forbidden)}
   {:pattern #"^/events"
    :handler authenticated?
    :on-error (fn [_ _] forbidden)}
   {:pattern #".*(/.*|$)"
    :handler authenticated?
    :redirect "/login"}])

(defn random-token
  []
  (let [randomdata (nonce/random-bytes 16)]
    (codecs/bytes->hex randomdata)))

(defn handle-login [request]
  (let [form-params (-> (:form-params request)
                        (walk/keywordize-keys))
        {:keys [chatrID]} form-params
        ;; 7776000 = 90 days
        max-age 7776000]
    (->
     (response/redirect "/")
     (response/set-cookie "token" (random-token) {:max-age max-age
                                                  :http-only true
                                                  :same-site :strict
                                                  :path "/"})
     (response/set-cookie "chatrID" chatrID {:max-age max-age
                                             :http-only true
                                             :same-site :strict
                                             :path "/"}))))

(defroutes app-routes
  (GET "/login" request (Login request))
  (GET "/" request (ChatRoom request))
  (GET "/login" request (Login request))
  (POST "/login" request (handle-login request))
  (GET "/logout" [] (clear-cookie))
  (GET "/ok" [] (response/response {:success true}))
  (route/resources "/")
  (route/not-found "Not Found"))

(def chatr
  (-> app-routes
      (wrap-defaults (-> site-defaults
                         (assoc :cookies false)))
      (wrap-access-rules {:rules access-rules})
      wrap-cookies))
