(ns chatr.server
  (:require [aleph.http :as http]
            [chatr.config :as config]
            [chatr.handler :refer [chatr]]))

(defonce server (atom nil))

(defn stop-server! []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (.close @server)
    (reset! server nil)))

(defn start-server!
  ([] (start-server! config/http-port))
  ([port]
   (stop-server!)
   (reset! server (http/start-server chatr {:port port}))))


