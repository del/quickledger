(ns quickledger.test.ledger-output
  (:use [quickledger.ledger-output])
  (:use [clojure.test]))

(deftest calc-amount-width-test
  (let [entries [{:account "Assets:Checking:Nordea"
                  :amount -100.00}
                 {:account "Expenses:Lolcats"
                  :amount 80.0}
                 {:account "Expenses:Vat"
                  :amount 20.0}]]
    (is (= (calc-amount-width entries) 8))))

(deftest calc-amount-width-with-integer-test
  (let [entries [{:account "Assets:Checking:Nordea"
                  :amount -100}
                 {:account "Expenses:Lolcats"
                  :amount 80}
                 {:account "Expenses:Vat"
                  :amount 20}]]
    (is (= (calc-amount-width entries) 8))))

(deftest calc-account-width-test
  (let [entries [{:account "Assets:Checking:Nordea"
                  :amount -100.00}
                 {:account "Expenses:Lolcats"
                  :amount 80.0}
                 {:account "Expenses:Vat"
                  :amount 20.0}]]
    (is (= (calc-account-width entries) 23))))

(deftest amount-with-padding-test
  (let [amounts    [-100.00 80.00 20.00]
        pad-length 8
        expected   [" -100.00"
                    "   80.00"
                    "   20.00"]]
    (is (= (map (fn [amount]
                  (amount-with-padding amount pad-length))
                amounts)
           expected))))

(deftest account-with-padding-test
  (let [accounts   ["Assets:Checking:Nordea"
                    "Expenses:Lolcats"
                    "Expenses:Vat"]
        pad-length 23
        expected   ["Assets:Checking:Nordea "
                    "Expenses:Lolcats       "
                    "Expenses:Vat           "]]
    (is (= (map (fn [account]
                  (account-with-padding account pad-length))
                accounts)
           expected))))

(deftest entry-to-str-test
  (let [entry         {:account "Assets:Checking:Nordea"
                       :amount -100.00}
        account-width 23
        amount-width  8
        expected      "  Assets:Checking:Nordea  -100.00"]
    (is (= (entry-to-str entry account-width amount-width)
           expected))))

(deftest entries-to-str-test
  (let [entries  [{:account "Assets:Checking:Nordea"
                   :amount -100.00}
                  {:account "Expenses:Lolcats"
                   :amount 80.0}
                  {:account "Expenses:Vat"
                   :amount 20.0}]
        expected ["  Assets:Checking:Nordea  -100.00"
                  "  Expenses:Lolcats          80.00"
                  "  Expenses:Vat              20.00"]]
    (is (= (entries-to-str entries) expected))))

(deftest transaction-to-str-test
  (let [transaction {:date "2011-08-26"
                     :desc "Lolcats"
                     :certain true
                     :entries [{:account "Assets:Checking:Nordea"
                                :amount -100.00}
                               {:account "Expenses:Lolcats"
                                :amount 80.0}
                               {:account "Expenses:Vat"
                                :amount 20.0}]}
        expected    ["2011-08-26 Lolcats"
                     "  Assets:Checking:Nordea  -100.00"
                     "  Expenses:Lolcats          80.00"
                     "  Expenses:Vat              20.00"
                     ""]]
    (is (= (transaction-to-str transaction) expected))))

(deftest transactions-to-str-test
  (let [transactions [{:date "2011-08-26"
                       :desc "Lolcats"
                       :certain true
                       :entries [{:account "Assets:Checking:Nordea"
                                  :amount -100.00}
                                 {:account "Expenses:Lolcats"
                                  :amount 80.0}
                                 {:account "Expenses:Vat"
                                  :amount 20.0}]}
                      {:date "2011-08-27"
                       :desc "Advice dogs"
                       :certain false
                       :entries [{:account "Assets:Checking:Nordea"
                                  :amount -200.00}
                                 {:account "Expenses:Raccoons"
                                  :amount 100.0}
                                 {:account "Expenses:Toiletries"
                                  :amount 100.0}]}]
        expected     (str "2011-08-26 Lolcats\n"
                          "  Assets:Checking:Nordea  -100.00\n"
                          "  Expenses:Lolcats          80.00\n"
                          "  Expenses:Vat              20.00\n"
                          "\n"
                          "!!!2011-08-27 Advice dogs\n"
                          "  Assets:Checking:Nordea  -200.00\n"
                          "  Expenses:Raccoons        100.00\n"
                          "  Expenses:Toiletries      100.00\n"
                          "\n")]
    (is (= (transactions-to-str transactions) expected))))