(ns firma-gen.core
    (:require [reagent.core :as r]
              [clojure.string :as s]))

;; -------------------------
;; Logic

(def vowels '("a" "e" "i" "o" "u" "y"))

(def consonants '("b" "c" "d" "f" "g" "h" "j" "l" "m" "n" "p" "q" "r" "s" "t" "v" "w" "x" "z"))

(defn- rand-chars [s amount]
  (let [indices (take amount (repeatedly #(rand-int (dec (count s)))))]
    (map #(nth s %) indices)))

(defn gen-name []
  (-> (interleave (rand-chars consonants 3) (rand-chars vowels 3)) s/join s/capitalize))

;; -------------------------
;; State

(def state (r/atom "Tämä on firmanimigeneraattori. Paina nappia!"))

;; -------------------------
;; Views

(defn home-page []
  [:div.jumbotron
    [:div [:h2 @state]
     [:br]
     [:button 
      {:class "btn btn-info" :on-click (fn [e] (reset! state (gen-name)))} 
      "Luo uusi"]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
