(ns quickledger.transaction-filter
  (:gen-class))

(defn select-match [raw-matches]
  (let [matches (remove nil? raw-matches)
        certain-match (first (filter (fn [match] (:certain match)) matches))]
    (if (nil? certain-match)
      (if (empty? matches) nil matches)
      certain-match)))

(defn calc-transfer [full-amount transfer]
  (let [amount (:amount transfer)]
    (assoc transfer
      :amount
      (if (and (<= 0.0 amount) (<= amount 1.0))
        (- (* amount full-amount))
        amount))))

(defn calc-transfers [first-transfer filter-transfers]
  (let [full-amount (:amount first-transfer)]
    (cons first-transfer
          (map (fn [transfer]
                 (calc-transfer full-amount transfer))
               filter-transfers))))

(defn add-transfers-to-transaction [trans filter-transfers]
  (let [first-transfer (first (:transfers trans))]
    (assoc
      trans
      :transfers (calc-transfers first-transfer filter-transfers))))

(defn apply-filter-to-transaction [trans filter]
  (if (= nil (re-find
              (:regex filter)
              (:desc trans)))
    nil
    (let [trans-with-transfers
          (assoc
              (add-transfers-to-transaction
               trans
               (:transfers filter))
            :certain (:certain filter))]
      (if (nil? (:desc filter))
        trans-with-transfers
        (assoc trans-with-transfers
          :desc (:desc filter))))))

(defn filter-transaction [trans filters]
  (let [match
        (select-match (map (fn [filter]
                             (apply-filter-to-transaction trans filter))
                           filters))]
    (if (nil? match) (assoc trans :certain false) match)))

(defn filter-transactions [transactions filters]
  (remove nil? (map (fn [trans]
                      (filter-transaction trans filters))
                    transactions)))

(defn read-filters [filename]
  (load-file filename))