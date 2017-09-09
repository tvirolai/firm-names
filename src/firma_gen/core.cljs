(ns firma-gen.core
    (:require [reagent.core :as r]
              [clojure.string :as s]
              [firma-gen.data :as d]
              [re-frame.core :as rf]))

(def log (.-log js/console))

;; -------------------------
;; Logic

; Generate random names

(def vowels '("a" "e" "i" "o" "u" "y"))

(def consonants '("b" "c" "d" "f" "g" "h" "j" "l" "m" "n" "p" "q" "r" "s" "t" "v" "w" "x" "z"))

(defn- rand-chars [s amount]
  (let [indices (take amount (repeatedly #(rand-int (dec (count s)))))]
    (map #(nth s %) indices)))

(defn gen-name! []
  (-> (interleave (rand-chars consonants 3) (rand-chars vowels 3)) s/join s/capitalize))

; Generate names using character probabilities from d/trans-table

(defn- pick-char [table-item number]
  (let [entry (filter
                (fn [item]
                  (let [[[n1 n2] _] item]
                    (<= n1 number n2)))
                table-item)]
    (->> entry
         flatten
         last)))

(defn gen-prob-name!
  "Generate a name based on the probablities in d/trans-table."
  []
  (let [rand-floats (take 6 (repeatedly #(rand)))]
    (s/capitalize (s/join (map pick-char d/trans-table rand-floats)))))

;; Re-frame stuff

;; -------------------------
;; Event dispatch


;; -------------------------
;; Event handlers

(rf/reg-event-db
  :initialize
  (fn [_ _]
    {:text "Tämä on firmanimigeneraattori. Paina nappia!"
     :gen-f gen-prob-name!}))

(rf/reg-event-db
  :change-text
  (fn [db [_ new-text]]
    (assoc db :text new-text)))

(rf/reg-event-db
  :toggle-function
  (fn [db _]
    (if (= gen-prob-name! (:gen-f db))
      (assoc db :gen-f gen-name!)
      (assoc db :gen-f gen-prob-name!))))

;; -------------------------
;; Query

(rf/reg-sub
  :text
  (fn [db _]
    (:text db)))

(rf/reg-sub
  :gen-f
  (fn [db _]
    (:gen-f db)))

;; -------------------------
;; View functions

(defn text []
  ;[:h2 @state])
  [:h2 @(rf/subscribe [:text])])

(defn button [gen-f]
  [:button
  {:class "btn btn-outline-info"
   :on-click #(rf/dispatch [:change-text (gen-f)])}
  "Luo uusi!"])

(defn ui []
  [:div.jumbotron
    [:div
     [text]
     [:br]
      [:div {:class "col-auto"}
       [:label {:class "custom-control custom-checkbox mb-2 mr-sm-2 mb-sm-0"}
        [:input {:type "checkbox"
                 :class "custom-control-input"
                 :on-change #(rf/dispatch [:toggle-function])}]
        [:span {:class "custom-control-indicator"}]
        [:span {:class "custom-control-description"}] "Satunnainen"]
       ]]
     [:br]
     [button @(rf/subscribe [:gen-f])]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (rf/dispatch-sync [:initialize])
  (r/render [ui] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
