(ns chatr.dev
  (:require [chatr.server :refer [start-server!]]))

(defn dev-init []
  (start-server!))
