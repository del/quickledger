(ns quickledger.core
  (:require quickledger.nordea-parser)
  (:require quickledger.transaction-filter)
  (:require quickledger.ledger-output)
  (:use clojure.tools.cli))

(defn apply-filter-file-to-csv-file [filter-file
                                     csv-file
                                     account-name
                                     csv-read-function]
  (let [transactions (csv-read-function csv-file account-name)
        filters (quickledger.transaction-filter/read-filters filter-file)]
    (quickledger.transaction-filter/filter-transactions
     transactions filters)))

(defn apply-filter-file-to-csv-file-and-save-to-ledger-file [filter-file
                                                             csv-file
                                                             ledger-file
                                                             account-name
                                                             csv-read-function]
  (let [filtered-transactions (apply-filter-file-to-csv-file filter-file
                                                             csv-file
                                                             account-name
                                                             csv-read-function)
        stats {:number-of-transactions (count filtered-transactions)
               :number-of-entries (count (flatten filtered-transactions))}]
    (quickledger.ledger-output/print-transactions-to-file ledger-file
                                                          filtered-transactions)
    stats))

(def parsing-functions
  {"nordea" quickledger.nordea-parser/read-csv})

(defn -main [& args]
  (let [parsed-args
        (cli args
             (required ["-a" "--account" "The account the transactions come from"])
             (required ["-i" "--input" "The input file of transactions"])
             (required ["-o" "--output" "The output ledger file"])
             (required ["-f" "--filters" "The filter file to use"])
             (required ["-p" "--parser" "The parser to apply to the file" :default "nordea"]))
        stats (apply-filter-file-to-csv-file-and-save-to-ledger-file
               (:filters parsed-args)
               (:input parsed-args)
               (:output parsed-args)
               (:account parsed-args)
               (get parsing-functions (:parser parsed-args)))]
  (println "Wrote" (:number-of-entries stats) "entries from"
           (:number-of-transactions stats) "transactions to"
           (:output parsed-args))))