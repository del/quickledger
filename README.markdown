# quickledger v1.2.0

quickledger is a tool to allow easy bookkeeping. It will allow you to setup filters which automatically assign transactions to accounts, or suggest accounts and let the user interactively approve/reject the suggestions. The output is a ledger file, for use with the ledger bookkeeping tool.

## Installation

### From binary
[Download the binary](https://github.com/downloads/del/quickledger/quickledger-1.2.0-standalone.jar "quickledger-1.2.0-standalone.jar") and use it as described below.

### From source
Assuming you're using leiningen, the steps are:

1. Clone the source repository.

        git clone http://github.com/del/quickledger

2. Install dependencies.

        lein deps

3. Compile.

        lein compile

4. Create a neat standalone .jar file.

        lein uberjar

5. Done! You can now use quickledger as described below.

## Usage
quickledger is simple to use, you just need to specify the input file, the name of the account it relates to, the output file, the filters file and the parser to use. At this time, the only available parser is nordea.

    java -jar quickledger-1.2.0-standalone.jar -i export.csv -a "Assets:Checking:Nordea" -f filters.clj -p nordea -o ledgerfile.dat

## Filter files
Filters are stored in a file with Clojure syntax, and the easiest way to get started is to refer to the file data/testfilters.clj for examples.

A filter file contains a list of filters, each filter being a map data structure with the following syntax:

    {:certain true
     :regex #"cats"
     :desc "Cat with gherkins"
     :transfers [{:account "Expenses:Lolcats"
                  :amount 0.8}
                 {:account "Expenses:Vat"
                  :amount 0.2}]}

The `:regex` variable is a regular expression that matches against the description of a transaction. If a match is found, the filter gets applied to that transaction.

If `:certain` is set to true, the resulting transaction gets inserted into the ledger output file, and no other filters that match the transaction will be considered. If more than one `:certain` filter matches a transaction, the first one is used.

If `:certain` is false, the resulting transaction gets output prepended by three exclamation marks (!!!), and other filters are also considered.

If the `:desc` variable is set, the description from the input file is discarded in favour of the filter's description.

Finally, the `:transfers` variable holds a list of transfers that setup the "other side" of the transaction. Each transfer is a simple map with a variable `:account` which is the account that transfer applies to, and `:amount`, which specifies the amount of the transfer.

If `:amount` is between 0.0 and 1.0, it is considered a fraction of the transaction amount, and is multiplied with the negative of the transaction amount. I.e. when specifying an `:amount` of 0.5 for a transaction of value 100.00, the transfer gets the value -50.00.

If `:amount` is any other number, it is taken as is, and without negation, i.e. an `:amount` of 50.00 will give exactly that in the transfer.

It is up to the user to ensure the amounts specified in the filter will sum to the full value of the transaction, or the resulting ledger file will be broken due to accounts not balancing.

As a final example, applying the filter above to this transaction:

    2011-09-12,Lolcats,Assets:Checking:Nordea,-100.00

would generate this output:

    2011-09-12 Cat with gherkins
      Assets:Checking:Nordea  -100.00
      Expenses:Lolcats          80.00
      Expenses:Vat              20.00

## License

Copyright (C) 2011 Daniel Eliasson

Distributed under the Eclipse Public License, the same as Clojure.
