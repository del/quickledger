(ns quickledger.transaction-filter)

(defn select-match [raw-matches]
  (let [matches (remove nil? raw-matches)
        certain-match (first (filter (fn [match] (:certain match)) matches))]
    (if (nil? certain-match)
      matches
      certain-match)))

(defn calc-transfer [full-amount transfer]
  (assoc
    transfer
    :amount (- (* (:amount transfer) full-amount))))

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
    (assoc
      (add-transfers-to-transaction
       trans
       (:transfers filter))
      :certain (:certain filter))))

(defn filter-transaction [trans filters]
  (select-match (map (fn [filter]
                       (apply-filter-to-transaction trans filter))
                     filters)))

(defn filter-transactions [transactions filters]
  (map (fn [trans]
         (filter-transaction trans filters))
       transactions))

(defn read-filters [filename]
  (load-file filename))