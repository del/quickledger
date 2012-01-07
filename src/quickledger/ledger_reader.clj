(ns quickledger.ledger-reader
  (:gen-class)
  (:require [clojure.string :as str]))

(defn is-part-of-entry? [line]
  (not (empty? line)))

(defn grab-entry [entries lines]
  (if (empty? lines)
    [entries lines]
    (let [grab-tmp (split-with is-part-of-entry? lines)
          entry (grab-tmp 0)
          rest-lines (drop-while empty? (grab-tmp 1))]
      [(cons entry entries)
       rest-lines])))

(defn separate-entries [lines]
  (loop [acc [] remaining-lines lines]
    (if (empty? remaining-lines)
      acc
      (let [grab-tmp (grab-entry acc remaining-lines)]
        (recur (grab-tmp 0) (grab-tmp 1))))))

(defn parse-date-and-description [line]
  (let [pieces (str/split (str/trim line) #"\s" 2)
        date   (str/trim (pieces 0))
        desc   (str/trim (pieces 1))]
    {:date date
     :desc desc}))

(defn parse-transfer [line]
  (let [trim-line (str/trim line)
        pieces     (str/split trim-line #"\s")
        amount-str (str/trim (last pieces))
        amount     (Float/parseFloat amount-str) 
        account    (str/trim (str/replace-first trim-line
                                                amount-str
                                                ""))]
    {:amount amount
     :account account}))

(defn parse-transfers [lines]
  (map parse-transfer lines))

(defn parse-entry [entry]
  (let [date-and-desc (parse-date-and-description (first entry))
        transfers     (parse-transfers (rest entry))]
    (assoc date-and-desc
           :transfers transfers
           :certain true)))

(defn parse-entries [entries]
  (map parse-entry entries))

(defn parse [lines]
  (parse-entries (separate-entries lines)))

(defn read-file [filename]
  (parse (str/split-lines (slurp filename))))