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

;; -------------------------
;; State

(def state (r/atom "Tämä on firmanimigeneraattori. Paina nappia!"))

;; Re-frame stuff

;; -------------------------
;; Event dispatch


;; -------------------------
;; Event handlers

;; -------------------------
;; Query

;; -------------------------
;; View functions


;; -------------------------
;; Views

(defn home-page []
  [:div.jumbotron
    [:div [:h2 @state]
     [:br]
     [:button
      {:class "btn btn-info" :on-click (fn [e] (reset! state (gen-name!)))}
      "Luo uusi"]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
