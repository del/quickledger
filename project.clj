(defproject quickledger "1.0.0"
  :description "quickledger"
  :url "http://github.com/del/quickledger"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [clojure-csv/clojure-csv "1.3.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/tools.cli "0.1.0"]]
  :dev-dependencies [[swank-clojure "1.3.2"]]
  :main quickledger.core)