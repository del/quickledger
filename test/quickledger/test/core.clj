(ns quickledger.test.core
  (:use [quickledger.core])
  (:use [clojure.test]))

(deftest read-nordea-csv-test
  (is (= (read-nordea-csv "testdata/test.csv" "Assets:Checking:Nordea")
         (list {:date "2011-08-26"
                :desc "Advice dog"
                :amount (list 'debit 400.00)
                :account "Assets:Checking:Nordea"}
               {:date "2011-08-26"
                :desc "Lolcats"
                :amount (list 'debit 79.00)
                :account "Assets:Checking:Nordea"} ))))