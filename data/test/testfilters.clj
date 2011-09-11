[{:certain true
  :regex #"cats"
  :desc "Cat with gherkins"
  :transfers [{:account "Expenses:Lolcats"
               :amount 0.8}
              {:account "Expenses:Vat"
               :amount 0.2}]}
 {:certain false
  :regex #"dogs"
  :transfers [{:account "Expenses:Raccoons"
               :amount 0.5}
              {:account "Expenses:Toiletries"
               :amount 0.5}]}
 {:certain false
  :regex #"dogs"
  :transfers [{:account "Expenses:Goats"
               :amount 1.0}]}]