(ns quickledger.ledger-reader
  (:gen-class)
  (:require [clojure.string :as str]))

(defn is-part-of-transaction? [line]
  (not (empty? line)))

(defn grab-transaction [transactions lines]
  (if (empty? lines)
    [transactions lines]
    (let [grab-tmp (split-with is-part-of-transaction? lines)
          transaction (grab-tmp 0)
          rest-lines (drop-while empty? (grab-tmp 1))]
      [(cons transaction transactions)
       rest-lines])))

(defn separate-transactions [lines]
  (loop [acc [] remaining-lines lines]
    (if (empty? remaining-lines)
      acc
      (let [grab-tmp (grab-transaction acc remaining-lines)]
        (recur (grab-tmp 0) (grab-tmp 1))))))

(defn parse-date-and-description [line]
  (let [pieces (str/split (str/trim line) #"\s" 2)
        date   (str/trim (pieces 0))
        desc   (str/trim (pieces 1))]
    {:date date
     :desc desc}))

(defn parse-entry [line]
  (let [trim-line (str/trim line)
        pieces     (str/split trim-line #"\s")
        amount-str (str/trim (last pieces))
        amount     (Float/parseFloat amount-str) 
        account    (str/trim (str/replace-first trim-line
                                                amount-str
                                                ""))]
    {:amount amount
     :account account}))

(defn parse-entries [lines]
  (map parse-entry lines))

(defn parse-transaction [transaction]
  (let [date-and-desc (parse-date-and-description (first transaction))
        entries       (parse-entries (rest transaction))]
    (assoc date-and-desc
           :entries entries
           :certain true)))

(defn parse-transactions [transactions]
  (map parse-transaction transactions))

(defn parse [lines]
  (parse-transactions (separate-transactions lines)))

(defn read-file [filename]
  (parse (str/split-lines (slurp filename))))