(ns quickledger.core
  (:gen-class)
  (:require quickledger.ledger-reader)
  (:require quickledger.ledger-output)
  (:require quickledger.nordea-parser)
  (:require quickledger.seb-parser)
  (:require quickledger.transaction-filter)
  (:use clojure.tools.cli))

(defn sort-transactions [transactions]
  (sort-by :date transactions))

(defn apply-filter-file-to-csv-file [filter-file
                                     csv-file
                                     ledger-file
                                     account-name
                                     csv-read-function]
  (let [transactions (csv-read-function csv-file account-name)
        old-transactions (quickledger.ledger-reader/read-file ledger-file)
        filters (quickledger.transaction-filter/read-filters filter-file)]
    (sort-transactions (flatten (concat old-transactions
                                        (quickledger.transaction-filter/filter-transactions
                                         transactions filters))))))

(defn apply-filter-file-to-csv-file-and-save-to-ledger-file [filter-file
                                                             csv-file
                                                             ledger-file
                                                             account-name
                                                             csv-read-function]
  (let [filtered-transactions (apply-filter-file-to-csv-file filter-file
                                                             csv-file
                                                             ledger-file
                                                             account-name
                                                             csv-read-function)
        stats {:number-of-transactions (count filtered-transactions)
               :number-of-entries (count (flatten filtered-transactions))}]
    (quickledger.ledger-output/print-transactions-to-file ledger-file
                                                          filtered-transactions)
    stats))

(def parsing-functions
  {"nordea" quickledger.nordea-parser/read-csv
   "seb" quickledger.seb-parser/read-csv})

(defn -main [& args]
  (let [parsed-args
        (cli args
             (required ["-a" "--account" "The account the transactions come from"])
             (required ["-i" "--input" "The input file of transactions"])
             (required ["-o" "--output" "The output ledger file"])
             (required ["-f" "--filters" "The filter file to use"])
             (required ["-p" "--parser" "The parser to apply to the file"]))
        stats (apply-filter-file-to-csv-file-and-save-to-ledger-file
               (:filters parsed-args)
               (:input parsed-args)
               (:output parsed-args)
               (:account parsed-args)
               (get parsing-functions (:parser parsed-args)))]
  (println "Wrote" (:number-of-entries stats) "entries from"
           (:number-of-transactions stats) "transactions to"
           (:output parsed-args))))