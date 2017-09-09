(ns firma-gen.core
    (:require [reagent.core :as r]
              [clojure.string :as s]
              [firma-gen.data :as d]
              [re-frame.core :as rf]))

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
    {:text "Tämä on firmanimigeneraattori. Paina nappia!"}))

(rf/reg-event-db
  :change-text
  (fn [db [_ new-text]]
    (assoc db :text new-text)))

;; -------------------------
;; Query

(rf/reg-sub
  :text
  (fn [db _]
    (:text db)))

;; -------------------------
;; View functions

(defn text []
  ;[:h2 @state])
  [:h2 @(rf/subscribe [:text])])

(defn button []
  [:button
  {:class "btn btn-outline-info"
   :on-click #(rf/dispatch [:change-text (gen-prob-name!)])}
  "Luo uusi!"])

(defn ui []
  [:div.jumbotron
    [:div
     [text]
     [:br]
     [button]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (rf/dispatch-sync [:initialize])
  (r/render [ui] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
