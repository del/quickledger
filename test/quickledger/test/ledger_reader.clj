(ns quickledger.test.ledger-reader
  (:use [quickledger.ledger-reader])
  (:use [clojure.test])
  (:require [clojure.string :as str]))

(deftest read-file-test
  (is (= (read-file "data/test/test-ledger.dat")
         [{:date "2011-08-14"
           :desc "Lunch"
           :certain true
           :entries [{:amount -89.00
                      :account "Assets:Checking:SEB"}
                     {:amount 89.00
                      :account "Expenses:Dining:Lunch"}]}
          {:date "2011-09-13"
           :desc "Dinner"
           :certain true
           :entries [{:amount -75.00
                      :account "Assets:Checking:SEB"}
                     {:amount 75.00
                      :account "Expenses:Dining:Other"}]}
          {:date "2011-09-19"
           :desc "Public transport ticket"
           :certain true
           :entries [{:amount -790.00
                      :account "Assets:Checking:Nordea"}
                     {:amount 790.00
                      :account "Expenses:Transport:Public"}]}])))

(deftest grab-transaction-test
  (let [transactions  [["a1" "a2"]
                       ["b1" "b2"]]
        lines         ["c1" "c2" "" "" "" "d1" "d2" "" "e1" "e2"]
        expected      [[["c1" "c2"]
                        ["a1" "a2"]
                        ["b1" "b2"]]
                       ["d1" "d2" "" "e1" "e2"]]]
    (is (= (grab-transaction transactions lines) expected))))

(deftest separate-transactions-test
  (let [lines    (str/split-lines (slurp "data/test/test-ledger.dat"))
        expected [["2011-08-14 Lunch"
                   "  Assets:Checking:SEB     -89.00"
                   "  Expenses:Dining:Lunch    89.00"]
                  ["2011-09-13 Dinner"
                   "  Assets:Checking:SEB     -75.00"
                   "  Expenses:Dining:Other    75.00"]
                  ["2011-09-19 Public transport ticket"
                   "  Assets:Checking:Nordea     -790.00"
                   "  Expenses:Transport:Public   790.00"]]]
    (is (= (separate-transactions lines) expected))))

(deftest parse-date-and-description-test
  (let [line     "  2011-08-14 Lunch with person  "
        expected {:date "2011-08-14"
                  :desc "Lunch with person"}]
    (is (= (parse-date-and-description line) expected))))

(deftest parse-entry-test
  (let [line     "  Assets:Checking:SEB   -89.00  "
        expected {:amount -89.00
                  :account "Assets:Checking:SEB"}]
    (is (= (parse-entry line) expected))))

(deftest parse-entries-test
  (let [lines    ["  Assets:Checking:SEB   -89.00  "
                  "  Expenses:Dining:Lunch  89.00  "]
        expected [{:amount -89.00
                   :account "Assets:Checking:SEB"}
                  {:amount 89.00
                   :account "Expenses:Dining:Lunch"}]]
    (is (= (parse-entries lines) expected))))

(deftest parse-transaction-test
  (let [transaction ["2011-08-14 Lunch"
                     "  Assets:Checking:SEB     -89.00"
                     "  Expenses:Dining:Lunch    89.00"]
        expected    {:date "2011-08-14"
                     :desc "Lunch"
                     :certain true
                     :entries [{:amount -89.00
                                :account "Assets:Checking:SEB"}
                               {:amount 89.00
                                :account "Expenses:Dining:Lunch"}]}]
    (is (= (parse-transaction transaction) expected))))