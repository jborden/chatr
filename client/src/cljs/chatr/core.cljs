(ns chatr.core
  (:require [accountant.core :as accountant]
            [clerk.core :as clerk]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [reagent.session :as session]
            [reitit.frontend :as reitit]
            [chatr.semantic :refer [Menu]]
            ;; [thrive.ws :as ws]
            ))

(def state (r/atom {}))
;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/"
     ["" :index]]
    ;; ["/patient" :patient]
    ;; ["/practice" :practice]
    ]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

;; main page navigation
(defn PageNav []
  (let [page-selection (r/cursor state [:page-selection])]
    (reset! page-selection (:route-key (session/get :route)))
    #_[:h1 "I don't do a whole lot yet"]
    ;; [Menu {:compact true
    ;;        :style {:margin-bottom "1rem"}}
    ;;  [MenuItem {:name "Patient"
    ;;             :active (= @page-selection :patient)
    ;;             :onClick (fn [_]
    ;;                        (reset! page-selection :patient)
    ;;                        (accountant/navigate! "/patient"))}]
    ;;  [MenuItem {:name "Practice"
    ;;             :active (= @page-selection :practice)
    ;;             :onClick (fn [_]
    ;;                        (reset! page-selection :practice)
    ;;                        (accountant/navigate! "/practice"))}]]
    ))

(defn PageWrapper [content]
  [:div
   [:div {:class "ui container"}
    #_[PageNav]
    content
    [:footer]]])

(defn home-page []
  (let [query-params (:query-params (session/get :route))
        page-selection (r/cursor state [:page-selection])]
    [PageWrapper
     [:h1 "Chatrs"]]
    ;; (when query-params
    ;;   (accountant/navigate! "/"))
    ;; (reset! page-selection (:route-key (session/get :route)))
    ;; #_(accountant/navigate! "/patient")
    ;; nil
    ))

#_(defn patient-page []
    [PageWrapper [:div
                  [PatientSearch]
                  [:div {:style {:margin-top "1em"}}
                   (when-not (nil? @current-patient)
                     [Patient @current-patient])]]])

#_(defn practice-page []
    [PageWrapper [Practice]])

;; -------------------------
;; Translate routes -> page components
(defn page-for [route]
  (case route
    :index #'home-page
    ;; :patient #'patient-page
    ;; :practice #'practice-page
    ))

;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [page])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn ^:export ^:dev/after-load init! []
  (clerk/initialize!)
  ;; setup our ws connection
  #_(ws/persist-ws)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)
            query-params (:query-params match)]
        (r/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params
                              :query-params query-params
                              :route-key current-page})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))

