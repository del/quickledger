(ns quickledger.core)

(require 'clojure-csv.core)

(defn filter-nordea-csv [csvline, account]
  {:date (nth csvline 0)
   :desc (nth csvline 1)
   :amount (nth csvline 3)
   :account 'account})

(defn read-nordea-csv [filename, account]
  (rest (map filter-nordea-csv
             (clojure-csv.core/parse-csv (slurp filename))
             account)))