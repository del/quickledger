(ns quickledger.ledger-output
  (:gen-class)
  (:require clojure.contrib.io))

(defn calc-amount-width [transfers]
  (+ 1 (reduce max (map
                    (fn [transfer]
                      (.length (format "%.2f" (float (:amount transfer)))))
                    transfers))))

(defn calc-account-width [transfers]
  (+ 1 (reduce max (map (fn [transfer]
                          (.length (:account transfer)))
                        transfers))))

(defn amount-with-padding [amount pad-length]
  (format (format "%% %d.2f" pad-length) (float amount)))

(defn account-with-padding [account pad-length]
  (format (format "%%-%ds" pad-length) account))

(defn transfer-to-str [transfer account-width amount-width]
  (str
   "  "
   (account-with-padding (:account transfer) account-width)
   (amount-with-padding (:amount transfer) amount-width)))

(defn transfers-to-str [transfers]
  (let [account-width (calc-account-width transfers)
        amount-width (calc-amount-width transfers)]
    (map (fn [transfer] (transfer-to-str
                         transfer
                         account-width
                         amount-width))
         transfers)))

(defn trans-header [trans]
  (str (if (:certain trans) "" "!!!")
       (:date trans) " " (:desc trans)))

(defn trans-to-str [trans]
  (conj
   (vec (cons (trans-header trans)
              (transfers-to-str (:transfers trans))))
   ""))

(defn transactions-to-str [transactions]
  (apply str (map
              (fn [string] (str string "\n"))
              (flatten (map trans-to-str (flatten transactions))))))

(defn print-transactions-to-file [filename transactions]
  (with-open [file (clojure.java.io/writer
                    (clojure.java.io/file filename) :append true)]
    (spit file (transactions-to-str transactions))))