(defproject quickledger "1.0.0-SNAPSHOT"
  :description "quickledger"
  :dependencies [
                 [org.clojure/clojure "1.2.1"]
                 [clojure-csv/clojure-csv "1.3.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/tools.cli "0.1.0"]
                 ]
  :dev-dependencies [[swank-clojure "1.3.2"]]
  :aot [quickledger.ledger-output
        quickledger.nordea-parser
        quickledger.transaction-filter]
  :main quickledger.core)