(ns quickledger.test.core
  (:use [quickledger.core])
  (:use [clojure.test]))

(deftest read-nordea-csv-test
  (is (= (read-nordea-csv "testdata/test.csv" "checking")
         ({:date "2011-08-26"
           :desc "Advice dog"
           :amount "-400.00"
           :account "checking"}
          {:date "2011-08-26"
           :desc "Advice dog"
           :amount "-400.00"
           :account "checking"}))))