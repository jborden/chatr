(defproject chatr "0.1.0-SNAPSHOT"
  :description "An HTML5 chat application"
  :url "https://chatr.chat"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [aleph "0.4.7-alpha7"]
                 [compojure "1.6.1"]
                 [environ "1.2.0"]
                 [hiccup "2.0.0-alpha2"]
                 [buddy/buddy-auth "2.2.0"]
                 [buddy/buddy-core "1.8.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-mock "0.4.0"]]
  :repl-options {:init-ns chatr.dev
                 :init (dev-init)}
  :source-paths ["src/clj"]
  :profiles
  {:prod {:main chatr.main
          :aot [chatr.main]
          :uberjar-name "chatr-server.jar"}})
