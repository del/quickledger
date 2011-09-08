(ns quickledger.test.core
  (:use [quickledger.core])
  (:use [quickledger.nordea-parser])
  (:use [clojure.test]))

(deftest apply-filter-file-to-csv-file-test
  (let [filter-file "data/test/testfilters.clj"
        csv-file "data/test/testcsv.csv"
        account-name "Assets:Checking:Nordea"
        expected [[{:date "2011-08-27"
                    :desc "Advice dogs"
                    :certain false
                    :transfers [{:account "Assets:Checking:Nordea"
                                 :amount -200.00}
                                {:account "Expenses:Raccoons"
                                 :amount 100.0}
                                {:account "Expenses:Toiletries"
                                 :amount 100.0}]}
                   {:date "2011-08-27"
                    :desc "Advice dogs"
                    :certain false
                    :transfers [{:account "Assets:Checking:Nordea"
                                 :amount -200.00}
                                {:account "Expenses:Goats"
                                 :amount 200.0}]}]
                  {:date "2011-08-26"
                   :desc "Lolcats"
                   :certain true
                   :transfers [{:account "Assets:Checking:Nordea"
                                :amount -100.00}
                               {:account "Expenses:Lolcats"
                                :amount 80.0}
                               {:account "Expenses:Vat"
                                :amount 20.0}]}]]
    (is (= (apply-filter-file-to-csv-file
            filter-file
            csv-file
            account-name
            quickledger.nordea-parser/read-csv)
           expected))))