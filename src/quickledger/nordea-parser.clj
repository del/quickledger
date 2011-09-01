(ns quickledger.nordea-parser)

(require 'clojure-csv.core)

(defn amount-from-account [amount]
  (if (< amount 0)
    (list 'debit (- amount))
    (list 'credit amount)))

(defn filter-nordea-csv [csvline account]
  {:date (nth csvline 0)
   :desc (nth csvline 1)
   :amount (amount-from-account (Float/parseFloat (nth csvline 3)))
   :account account})

(defn read-nordea-csv [filename account]
  (map (fn [csvline] (filter-nordea-csv csvline account))
       (rest (clojure-csv.core/parse-csv (slurp filename)))))