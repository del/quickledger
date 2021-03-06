(ns quickledger.nordea-parser
  (:gen-class)
  (:require clojure-csv.core))

(defn filter-nordea-csv [csvline account]
  {:date    (nth csvline 0)
   :desc    (nth csvline 1)
   :entries [{:amount (Float/parseFloat (nth csvline 3))
              :account account}]})

(defn read-csv [filename account]
  (map (fn [csvline] (filter-nordea-csv csvline account))
       (rest (clojure-csv.core/parse-csv (slurp filename)))))