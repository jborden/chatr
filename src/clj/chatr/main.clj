(ns chatr.main
  (:gen-class)
  (:require [chatr.config :refer [http-port]]
            [chatr.server :refer [start-server!]]))

(defn -main []
  (start-server!)
  (println "Server Running on port:" http-port))
