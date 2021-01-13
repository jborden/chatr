(ns chatr.html
  (:require [clojure.walk :refer [keywordize-keys]]
            [hiccup.page :as page :refer [html5]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))

(defn template [& body]
  (html5 [:head [:title "chatr"]
          [:link {:rel "stylesheet"
                  :href "//unpkg.com/semantic-ui@2.4.2/dist/semantic.min.css"}]
          [:link {:rel "stylesheet"
                  :href "css/main.css"}]
          [:link {:rel "shortcut icon"
                  :type "image/x-icon"
                  :href "/favicon/favicon.ico"}]
          ;;https://stackoverflow.com/questions/21473515/why-csrf-token-should-be-in-meta-tag-and-in-cookie
          [:meta {:name "csrf-token" :content *anti-forgery-token*}]
          [:meta {:name "viewport" :content "width=device-width, initial-scale=1, minimum-scale=1"}]]
         [:body
          body]))

(defn Root
  [el]
  (template [:div
             [:div {:class "ui masthead segment"
                    :style "padding: 0.25em"}
              [:div {:class "ui container"}
               [:div {:class "ui header"}
                [:a {:href "/"}
                 [:img {:src "images/logo.png"
                        :height "50px"}]]
                [:a {:href "/logout"
                     :style "float: right; position: relative; margin-top: 1.50rem;"}
                 "Logout"]]]]
             (page/include-js "js/app.js")
             el]))

(defn Login
  [request]
  (let [{:keys [error]} (-> (:query-params request)
                            keywordize-keys)]
    (template
     [:div {:id "login"}
      [:div {:class "ui stackable middle aligned center aligned grid"
             :style "margin: 1rem"}
       [:div {:class "column"}
        [:h2 {:class "ui blue image header"}
         [:img {:src "images/logo.png", :class "image"}]]
        [:form {:class "ui large form"
                :method "post"}
         [:div {:class "ui segment"}
          [:div {:class "field"}
           [:div {:class "ui left icon input"}
            [:i {:class "user icon"}]
            [:input {:type "text", :name "chatrID", :placeholder "How you will be called this time on chatr"}]]]
          #_[:div {:class "field"}
             [:div {:class "ui left icon input"}
              [:i {:class "lock icon"}]
              [:input {:type "password", :name "password", :placeholder "Password"}]]]
          (when error [:div {:style "color:red; padding-top: 1em;"}
                       error])
          [:input {:type "submit"
                   :value "Start Chatting"
                   :class "ui fluid large blue submit button"}]
          [:input {:type "hidden"
                   :name "__anti-forgery-token"
                   :id "__anti-forgery-token"
                   :value (force *anti-forgery-token*)}]
          ]]]]])))
