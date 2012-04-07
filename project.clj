(defproject rambo-kill-stats "0.0.1"
  :description "Visualization of critical data on the Rambo movie series"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [compojure "1.0.1"]
                 [hiccup "1.0.0-RC1"]
                 [ring "1.0.2"]
                 [incanter/incanter-core "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [incanter/incanter-io "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [incanter/incanter-charts "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [incanter/incanter-processing "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [incanter/incanter-mongodb "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [incanter/incanter-pdf "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [incanter/incanter-latex "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [incanter/incanter-excel "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [swingrepl "1.3.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojure-contrib]]
                 [jline "0.9.94"]]
  :source-path "src/main/clojure/"
  :resources-path "src/main/resources/"
  :main webapp)

