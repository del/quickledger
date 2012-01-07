(ns quickledger.test.transaction-filter
  (:use [quickledger.transaction-filter])
  (:use [clojure.test]))

(deftest select-match-certain-test
  (let [matches [{:certain false
                  :dummy 1}
                 {:certain false
                  :dummy 2}
                 {:certain true
                  :dummy 3}
                 {:certain false
                  :dummy 4}]]
    (is (= (select-match matches) {:certain true :dummy 3}))))

(deftest select-match-uncertain-test
  (let [matches [{:certain false
                  :dummy 1}
                 nil
                 {:certain false
                  :dummy 2}
                 nil
                 {:certain false
                  :dummy 3}]]
    (is (= (select-match matches) (remove nil? matches)))))

(deftest calc-entry-relative-amount-test
  (let [entry    {:account "Expenses:Lolcats"
                  :amount 0.8}
        expected {:account "Expenses:Lolcats"
                  :amount 80.0}]
    (is (= (calc-entry -100.0 entry) expected))))

(deftest calc-entry-fixed-amount-test
  (let [entry    {:account "Expenses:Lolcats"
                  :amount 80.0}
        expected {:account "Expenses:Lolcats"
                  :amount 80.0}]
    (is (= (calc-entry -100.0 entry) expected))))

(deftest calc-entries-relative-amounts-test
  (let [first-entry    {:account "Assets:Checking:Nordea"
                        :amount -100.0}
        filter-entries [{:account "Expenses:Lolcats"
                         :amount 0.8}
                        {:account "Expenses:Vat"
                         :amount 0.2}]
        expected       [{:account "Assets:Checking:Nordea"
                         :amount -100.0}
                        {:account "Expenses:Lolcats"
                         :amount 80.0}
                        {:account "Expenses:Vat"
                         :amount 20.0}]]
    (is (= (calc-entries first-entry filter-entries)
           expected))))

(deftest calc-entries-fixed-amounts-test
  (let [first-entry    {:account "Assets:Checking:Nordea"
                        :amount 100.0}
        filter-entries [{:account "Income:Lolcat sales"
                         :amount -180.0}
                        {:account "Expenses:Income taxes"
                         :amount 80.0}]
        expected       [{:account "Assets:Checking:Nordea"
                         :amount 100.0}
                        {:account "Income:Lolcat sales"
                         :amount -180.0}
                        {:account "Expenses:Income taxes"
                         :amount 80.0}]]
    (is (= (calc-entries first-entry filter-entries)
           expected))))

(deftest add-entries-to-transaction-test
  (let [transaction    {:date "2011-08-26"
                        :desc "Lolcats"
                        :entries [{:amount -100.00
                                   :account "Assets:Checking:Nordea"}]}
        filter-entries [{:account "Expenses:Lolcats"
                         :amount 0.8}
                        {:account "Expenses:Vat"
                         :amount 0.2}]
        expected       {:date "2011-08-26"
                        :desc "Lolcats"
                        :entries [{:account "Assets:Checking:Nordea"
                                   :amount -100.00}
                                  {:account "Expenses:Lolcats"
                                   :amount 80.0}
                                  {:account "Expenses:Vat"
                                   :amount 20.0}]}]
    (is (= (add-entries-to-transaction transaction filter-entries)
           expected))))

(deftest apply-filter-to-transaction-test
  (let [transaction {:date "2011-08-26"
                     :desc "Lolcats"
                     :entries [{:amount -100.00
                                :account "Assets:Checking:Nordea"}]}
        filter      {:certain true
                     :regex #"cats"
                     :entries [{:account "Expenses:Lolcats"
                                :amount 0.8}
                               {:account "Expenses:Vat"
                                :amount 0.2}]}
        expected    {:date "2011-08-26"
                     :desc "Lolcats"
                     :certain true
                     :entries [{:account "Assets:Checking:Nordea"
                                :amount -100.00}
                               {:account "Expenses:Lolcats"
                                :amount 80.0}
                               {:account "Expenses:Vat"
                                :amount 20.0}]}]
    (is (= (apply-filter-to-transaction transaction filter)
           expected))))

(deftest filter-transaction-certain-test
  (let [transaction {:date "2011-08-26"
                     :desc "Lolcats"
                     :entries [{:amount -100.00
                                :account "Assets:Checking:Nordea"}]}
        filters     [{:certain true
                      :regex #"cats"
                      :entries [{:account "Expenses:Lolcats"
                                 :amount 0.8}
                                {:account "Expenses:Vat"
                                 :amount 0.2}]}
                     {:certain false
                      :regex #"cats"
                      :entries [{:account "Expenses:Raccoons"
                                 :amount 0.5}
                                {:account "Expenses:Toiletries"
                                 :amount 0.5}]}]
        expected     {:date "2011-08-26"
                      :desc "Lolcats"
                      :certain true
                      :entries [{:account "Assets:Checking:Nordea"
                                 :amount -100.00}
                                {:account "Expenses:Lolcats"
                                 :amount 80.0}
                                {:account "Expenses:Vat"
                                 :amount 20.0}]}]
    (is (= (filter-transaction transaction filters)
           expected))))

(deftest filter-transaction-uncertain-test
  (let [transaction {:date "2011-08-26"
                     :desc "Lolcats"
                     :entries [{:amount -100.00
                                :account "Assets:Checking:Nordea"}]}
        filters     [{:certain false
                      :regex #"cats"
                      :entries [{:account "Expenses:Lolcats"
                                 :amount 0.8}
                                {:account "Expenses:Vat"
                                 :amount 0.2}]}
                     {:certain false
                      :regex #"cats"
                      :entries [{:account "Expenses:Raccoons"
                                 :amount 0.5}
                                {:account "Expenses:Toiletries"
                                 :amount 0.5}]}]
        expected    [{:date "2011-08-26"
                      :desc "Lolcats"
                      :certain false
                      :entries [{:account "Assets:Checking:Nordea"
                                 :amount -100.00}
                                {:account "Expenses:Lolcats"
                                 :amount 80.0}
                                {:account "Expenses:Vat"
                                 :amount 20.0}]}
                     {:date "2011-08-26"
                      :desc "Lolcats"
                      :certain false
                      :entries [{:account "Assets:Checking:Nordea"
                                 :amount -100.00}
                                {:account "Expenses:Raccoons"
                                 :amount 50.0}
                                {:account "Expenses:Toiletries"
                                 :amount 50.0}]}]]
    (is (= (filter-transaction transaction filters)
           expected))))

(deftest filter-transaction-no-hits-test
  (let [transaction {:date "2011-08-26"
                     :desc "Lolcats"
                     :entries [{:amount -100.00
                                :account "Assets:Checking:Nordea"}]}
        filters     [{:certain true
                      :regex #"dogs"
                      :entries [{:account "Expenses:Lolcats"
                                 :amount 0.8}
                                {:account "Expenses:Vat"
                                 :amount 0.2}]}]
        expected    {:date "2011-08-26"
                     :desc "Lolcats"
                     :certain false
                     :entries [{:amount -100.00
                                :account "Assets:Checking:Nordea"}]}]
    (is (= (filter-transaction transaction filters)
           expected))))

(deftest filter-transactions-test
  (let [transactions [{:date "2011-08-26"
                       :desc "Lolcats"
                       :entries [{:amount -100.00
                                  :account "Assets:Checking:Nordea"}]}
                      {:date "2011-08-27"
                       :desc "Advice dogs"
                       :entries [{:amount -200.00
                                  :account "Assets:Checking:Nordea"}]}]
        filters      [{:certain true
                       :regex #"cats"
                       :entries [{:account "Expenses:Lolcats"
                                  :amount 0.8}
                                 {:account "Expenses:Vat"
                                  :amount 0.2}]}
                      {:certain false
                       :regex #"dogs"
                       :entries [{:account "Expenses:Raccoons"
                                  :amount 0.5}
                                 {:account "Expenses:Toiletries"
                                  :amount 0.5}]}
                      {:certain false
                       :desc "Dog with goatee"
                       :regex #"dogs"
                       :entries [{:account "Expenses:Goats"
                                  :amount 1.0}]}]
        expected     [{:date "2011-08-26"
                       :desc "Lolcats"
                       :certain true
                       :entries [{:account "Assets:Checking:Nordea"
                                  :amount -100.00}
                                 {:account "Expenses:Lolcats"
                                  :amount 80.0}
                                 {:account "Expenses:Vat"
                                  :amount 20.0}]}
                      [{:date "2011-08-27"
                        :desc "Advice dogs"
                        :certain false
                        :entries [{:account "Assets:Checking:Nordea"
                                   :amount -200.00}
                                  {:account "Expenses:Raccoons"
                                   :amount 100.0}
                                  {:account "Expenses:Toiletries"
                                   :amount 100.0}]}
                       {:date "2011-08-27"
                        :desc "Dog with goatee"
                        :certain false
                        :entries [{:account "Assets:Checking:Nordea"
                                   :amount -200.00}
                                  {:account "Expenses:Goats"
                                   :amount 200.0}]}]]]
    (is (= (filter-transactions transactions filters)
           expected))))

(deftest filter-transactions-using-file-test
  (let [transactions [{:date "2011-09-26"
                       :desc "Lolcats"
                       :entries [{:amount -100.00
                                  :account "Assets:Checking:Nordea"}]}
                      {:date "2011-08-27"
                       :desc "Advice dogs"
                       :entries [{:amount -200.00
                                  :account "Assets:Checking:Nordea"}]}]
        filterfile   "data/test/testfilters.clj"
        expected     [{:date "2011-09-26"
                       :desc "Cat with gherkins"
                       :certain true
                       :entries [{:account "Assets:Checking:Nordea"
                                  :amount -100.00}
                                 {:account "Expenses:Lolcats"
                                  :amount 80.0}
                                 {:account "Expenses:Vat"
                                  :amount 20.0}]}
                      [{:date "2011-08-27"
                        :desc "Advice dogs"
                        :certain false
                        :entries [{:account "Assets:Checking:Nordea"
                                   :amount -200.00}
                                  {:account "Expenses:Raccoons"
                                   :amount 100.0}
                                  {:account "Expenses:Toiletries"
                                   :amount 100.0}]}
                       {:date "2011-08-27"
                        :desc "Advice dogs"
                        :certain false
                        :entries [{:account "Assets:Checking:Nordea"
                                   :amount -200.00}
                                  {:account "Expenses:Goats"
                                   :amount 200.0}]}]]]
    (is (= (filter-transactions transactions (read-filters filterfile))
           expected))))