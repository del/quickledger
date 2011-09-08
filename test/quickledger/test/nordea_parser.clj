(ns quickledger.test.nordea-parser
  (:use [quickledger.nordea-parser])
  (:use [clojure.test]))

(deftest read-csv-test
  (is (= (read-csv "data/test/testcsv.csv" "Assets:Checking:Nordea")
         [{:date "2011-08-27"
           :desc "Advice dogs"
           :transfers [{:amount -200.00
                        :account "Assets:Checking:Nordea"}]}
          {:date "2011-08-26"
           :desc "Lolcats"
           :transfers [{:amount -100.00
                        :account "Assets:Checking:Nordea"}]}])))