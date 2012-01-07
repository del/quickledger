(ns quickledger.transaction-filter
  (:gen-class))

(defn select-match [raw-matches]
  (let [matches (remove nil? raw-matches)
        certain-match (first (filter (fn [match] (:certain match)) matches))]
    (if (nil? certain-match)
      (if (empty? matches) nil matches)
      certain-match)))

(defn calc-entry [full-amount entry]
  (let [amount (:amount entry)]
    (assoc entry
      :amount
      (if (and (<= 0.0 amount) (<= amount 1.0))
        (- (* amount full-amount))
        amount))))

(defn calc-entries [first-entry filter-entries]
  (let [full-amount (:amount first-entry)]
    (cons first-entry
          (map (fn [entry]
                 (calc-entry full-amount entry))
               filter-entries))))

(defn add-entries-to-transaction [transaction filter-entries]
  (let [first-entry (first (:entries transaction))]
    (assoc
      transaction
      :entries (calc-entries first-entry filter-entries))))

(defn apply-filter-to-transaction [transaction filter]
  (if (= nil (re-find
              (:regex filter)
              (:desc transaction)))
    nil
    (let [transaction-with-entries
          (assoc
              (add-entries-to-transaction
               transaction
               (:entries filter))
            :certain (:certain filter))]
      (if (nil? (:desc filter))
        transaction-with-entries
        (assoc transaction-with-entries
          :desc (:desc filter))))))

(defn filter-transaction [transaction filters]
  (let [match
        (select-match (map (fn [filter]
                             (apply-filter-to-transaction transaction filter))
                           filters))]
    (if (nil? match) (assoc transaction :certain false) match)))

(defn filter-transactions [transactions filters]
  (remove nil? (map (fn [transaction]
                      (filter-transaction transaction filters))
                    transactions)))

(defn read-filters [filename]
  (load-file filename))