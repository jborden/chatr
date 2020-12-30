(ns chatr.config
  (:require [environ.core :refer [env]]))

(defn get-property-or-env
  [property-name]
  (or (System/getProperty property-name)
      (System/getenv property-name)))

(defn dev-env? []
  (boolean (= (:environment env) "dev")))

(when (dev-env?)
  (System/setProperty "HTTP_PORT" (env :http-port)))

(def http-port (read-string (or (get-property-or-env "HTTP_PORT") "8080")))
