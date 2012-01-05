(ns quickledger.seb-parser
  (:gen-class)
  (:require [clojure-csv.core :as csv])
  (:require [clojure.string :as string]))

(defn filter-seb-csv [csvline account]
  {:date (nth csvline 0)
   :desc (string/capitalize (string/trim (nth csvline 3)))
   :transfers [{:amount (Float/parseFloat (nth csvline 4))
                :account account}]})

(defn header-or-empty? [index item]
  (or (< index 5) (< (count item) 6)))

(defn remove-csv-headers [csvlines]
  (keep-indexed (fn [index item]
                  (cond (header-or-empty? index item) nil
                        :else item))
                csvlines))

(defn read-csv [filename account]
  (map (fn [csvline] (filter-seb-csv csvline account))
       (remove-csv-headers (csv/parse-csv (slurp filename)))))