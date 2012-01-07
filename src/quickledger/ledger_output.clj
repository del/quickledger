(ns quickledger.ledger-output
  (:gen-class)
  (:require clojure.contrib.io))

(defn calc-amount-width [entries]
  (+ 1 (reduce max (map
                    (fn [entry]
                      (.length (format "%.2f" (float (:amount entry)))))
                    entries))))

(defn calc-account-width [entries]
  (+ 1 (reduce max (map (fn [entry]
                          (.length (:account entry)))
                        entries))))

(defn amount-with-padding [amount pad-length]
  (format (format "%% %d.2f" pad-length) (float amount)))

(defn account-with-padding [account pad-length]
  (format (format "%%-%ds" pad-length) account))

(defn entry-to-str [entry account-width amount-width]
  (str
   "  "
   (account-with-padding (:account entry) account-width)
   (amount-with-padding (:amount entry) amount-width)))

(defn entries-to-str [entries]
  (let [account-width (calc-account-width entries)
        amount-width (calc-amount-width entries)]
    (map (fn [entry] (entry-to-str
                         entry
                         account-width
                         amount-width))
         entries)))

(defn transaction-header [transaction]
  (str (if (:certain transaction) "" "!!!")
       (:date transaction) " " (:desc transaction)))

(defn transaction-to-str [transaction]
  (conj
   (vec (cons (transaction-header transaction)
              (entries-to-str (:entries transaction))))
   ""))

(defn transactions-to-str [transactions]
  (apply str (map
              (fn [string] (str string "\n"))
              (flatten (map transaction-to-str transactions)))))

(defn print-transactions-to-file [filename transactions]
  (with-open [file (clojure.java.io/writer
                    (clojure.java.io/file filename) :append false)]
    (spit file (transactions-to-str transactions))))