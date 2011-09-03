(ns quickledger.test.nordea-parser
  (:use [quickledger.nordea-parser])
  (:use [clojure.test]))

(deftest read-nordea-csv-test
  (is (= (read-nordea-csv "testdata/test.csv" "Assets:Checking:Nordea")
         [{:date "2011-08-26"
           :desc "Advice dog"
           :transfers [{:amount -400.00
                        :account "Assets:Checking:Nordea"}]}
          {:date "2011-08-26"
           :desc "Lolcats"
           :transfers [{:amount -79.00
                        :account "Assets:Checking:Nordea"}]}])))