(ns quickledger.ledger-output
  (:require clojure.contrib.io))

(defn calc-amount-width [transfers]
  (+ 1 (reduce max (map
                    (fn [transfer]
                      (.length (format "%.2f" (:amount transfer))))
                    transfers))))

(defn calc-account-width [transfers]
  (+ 1 (reduce max (map (fn [transfer]
                          (.length (:account transfer)))
                        transfers))))

(defn amount-with-padding [amount pad-length]
  (format (format "%% %d.2f" pad-length) amount))

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

(defn trans-to-str [trans]
  (conj
   (vec (cons (str (:date trans) " " (:desc trans))
              (transfers-to-str (:transfers trans))))
   ""))

(defn transactions-to-str [transactions]
  (flatten (map trans-to-str transactions)))

(defn print-transactions-to-file [filename transactions]
  (clojure.contrib.io/write-lines filename transactions))