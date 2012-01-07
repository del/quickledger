(ns quickledger.test.seb-parser
  (:use [quickledger.seb-parser])
  (:use [clojure.test]))

(deftest read-csv-test
  (is (= (read-csv "data/test/test-seb.csv" "Assets:Checking:SEB")
         [{:date "2011-12-14"
           :desc "Advice dogs"
           :entries [{:amount -200.00
                      :account "Assets:Checking:SEB"}]}
          {:date "2011-12-12"
           :desc "Lolcats"
           :entries [{:amount -100.00
                      :account "Assets:Checking:SEB"}]}])))