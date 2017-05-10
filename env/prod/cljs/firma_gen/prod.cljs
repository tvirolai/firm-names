(ns firma-gen.prod
  (:require [firma-gen.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
