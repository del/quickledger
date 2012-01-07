(ns quickledger.test.nordea-parser
  (:use [quickledger.nordea-parser])
  (:use [clojure.test]))

(deftest read-csv-test
  (is (= (read-csv "data/test/testcsv.csv" "Assets:Checking:Nordea")
         [{:date "2011-08-27"
           :desc "Advice dogs"
           :entries [{:amount -200.00
                      :account "Assets:Checking:Nordea"}]}
          {:date "2011-08-26"
           :desc "Lolcats"
           :entries [{:amount -100.00
                      :account "Assets:Checking:Nordea"}]}])))