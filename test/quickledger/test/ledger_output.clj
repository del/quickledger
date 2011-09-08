(ns quickledger.test.ledger-output
  (:use [quickledger.ledger-output])
  (:use [clojure.test]))

(deftest calc-amount-width-test
  (let [transfers [{:account "Assets:Checking:Nordea"
                    :amount -100.00}
                   {:account "Expenses:Lolcats"
                    :amount 80.0}
                   {:account "Expenses:Vat"
                    :amount 20.0}]]
    (is (= (calc-amount-width transfers) 8))))

(deftest calc-account-width-test
  (let [transfers [{:account "Assets:Checking:Nordea"
                    :amount -100.00}
                   {:account "Expenses:Lolcats"
                    :amount 80.0}
                   {:account "Expenses:Vat"
                    :amount 20.0}]]
    (is (= (calc-account-width transfers) 23))))

(deftest amount-with-padding-test
  (let [amounts [-100.00 80.00 20.00]
        pad-length 8
        expected [" -100.00"
                  "   80.00"
                  "   20.00"]]
    (is (= (map (fn [amount]
                  (amount-with-padding amount pad-length))
                amounts)
           expected))))

(deftest account-with-padding-test
  (let [accounts ["Assets:Checking:Nordea"
                  "Expenses:Lolcats"
                  "Expenses:Vat"]
        pad-length 23
        expected ["Assets:Checking:Nordea "
                  "Expenses:Lolcats       "
                  "Expenses:Vat           "]]
    (is (= (map (fn [account]
                  (account-with-padding account pad-length))
                accounts)
           expected))))

(deftest transfer-to-str-test
  (let [transfer {:account "Assets:Checking:Nordea"
                  :amount -100.00}
        account-width 23
        amount-width 8
        expected "  Assets:Checking:Nordea  -100.00"]
    (is (= (transfer-to-str transfer account-width amount-width)
           expected))))

(deftest transfers-to-str-test
  (let [transfers [{:account "Assets:Checking:Nordea"
                    :amount -100.00}
                   {:account "Expenses:Lolcats"
                    :amount 80.0}
                   {:account "Expenses:Vat"
                    :amount 20.0}]
        expected ["  Assets:Checking:Nordea  -100.00"
                  "  Expenses:Lolcats          80.00"
                  "  Expenses:Vat              20.00"]]
    (is (= (transfers-to-str transfers) expected))))

(deftest trans-to-str-test
  (let [trans {:date "2011-08-26"
               :desc "Lolcats"
               :certain true
               :transfers [{:account "Assets:Checking:Nordea"
                            :amount -100.00}
                           {:account "Expenses:Lolcats"
                            :amount 80.0}
                           {:account "Expenses:Vat"
                            :amount 20.0}]}
        expected ["2011-08-26 Lolcats"
                  "  Assets:Checking:Nordea  -100.00"
                  "  Expenses:Lolcats          80.00"
                  "  Expenses:Vat              20.00"
                  ""]]
    (is (= (trans-to-str trans) expected))))

(deftest transactions-to-str-test
  (let [transactions [{:date "2011-08-26"
                       :desc "Lolcats"
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
                                    :amount 100.0}]}]
        expected (str "2011-08-26 Lolcats\n"
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

(deftest transactions-to-str-with-lists-test
  (let [transactions [{:date "2011-08-26"
                       :desc "Lolcats"
                       :certain true
                       :transfers [{:account "Assets:Checking:Nordea"
                                    :amount -100.00}
                                   {:account "Expenses:Lolcats"
                                    :amount 80.0}
                                   {:account "Expenses:Vat"
                                    :amount 20.0}]}
                      [{:date "2011-08-27"
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
                                    {:account "Expenses:Raccoons"
                                     :amount 80.0}
                                    {:account "Expenses:Toiletries"
                                     :amount 120.0}]}]]
        expected (str "2011-08-26 Lolcats\n"
                      "  Assets:Checking:Nordea  -100.00\n"
                      "  Expenses:Lolcats          80.00\n"
                      "  Expenses:Vat              20.00\n"
                      "\n"
                      "!!!2011-08-27 Advice dogs\n"
                      "  Assets:Checking:Nordea  -200.00\n"
                      "  Expenses:Raccoons        100.00\n"
                      "  Expenses:Toiletries      100.00\n"
                      "\n"
                      "!!!2011-08-27 Advice dogs\n"
                      "  Assets:Checking:Nordea  -200.00\n"
                      "  Expenses:Raccoons         80.00\n"
                      "  Expenses:Toiletries      120.00\n"
                      "\n")]
    (is (= (transactions-to-str transactions) expected))))