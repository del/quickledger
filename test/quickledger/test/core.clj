(ns quickledger.test.core
  (:use [quickledger.core])
  (:use [quickledger.nordea-parser])
  (:use [clojure.test]))

(deftest sort-transactions-test
  (let [transactions [{:date "2011-01-01"}
                      {:date "2011-02-02"}
                      {:date "2010-03-03"}]
        expected [{:date "2010-03-03"}
                  {:date "2011-01-01"}
                  {:date "2011-02-02"}]]
    (is (= (sort-transactions transactions) expected))))

(deftest apply-filter-file-to-csv-file-test
  (let [filter-file "data/test/testfilters.clj"
        csv-file "data/test/testcsv.csv"
        ledger-file "data/test/test-ledger.dat"
        account-name "Assets:Checking:Nordea"
        expected [{:date "2011-08-14"
                   :desc "Lunch"
                   :certain true
                   :transfers [{:amount -89.00
                                :account "Assets:Checking:SEB"}
                               {:amount 89.00
                                :account "Expenses:Dining:Lunch"}]}
                  {:date "2011-08-26"
                   :desc "Cat with gherkins"
                   :certain true
                   :transfers [{:account "Assets:Checking:Nordea"
                                :amount -100.00}
                               {:account "Expenses:Lolcats"
                                :amount 80.0}
                               {:account "Expenses:Vat"
                                :amount 20.0}]}
                  {:date "2011-08-27"
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
                                :amount 200.0}]}
                  {:date "2011-09-13"
                   :desc "Dinner"
                   :certain true
                   :transfers [{:amount -75.00
                                :account "Assets:Checking:SEB"}
                               {:amount 75.00
                                :account "Expenses:Dining:Other"}]}
                  {:date "2011-09-19"
                   :desc "Public transport ticket"
                   :certain true
                   :transfers [{:amount -790.00
                                :account "Assets:Checking:Nordea"}
                               {:amount 790.00
                                :account "Expenses:Transport:Public"}]}]]
    (is (= (apply-filter-file-to-csv-file
            filter-file
            csv-file
            ledger-file
            account-name
            quickledger.nordea-parser/read-csv)
           expected))))