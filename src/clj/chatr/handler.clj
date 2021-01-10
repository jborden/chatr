(ns chatr.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] "<<chatr>> Speak Freely <<chatr>>")
  (route/not-found "Not Found"))

(def chatr
  (wrap-defaults app-routes site-defaults))
