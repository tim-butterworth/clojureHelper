(ns clojurehelper.utilities.parser-spec
  (:require [speclj.core :refer :all]
            [clojurehelper.utilities.parser :refer :all]))

(import java.io.File)

(def file-path (str (. (File. ".") getCanonicalPath) "spec/clojurehelper/utilities/testclojurefile"))

(def fun-str-one "(defn function1 [param1 param2])")
(def fun-str-two "(defn function1 [param1 param2] \n(* param1 param2))")

(describe 
 "find-all-functions"

 (before (println (str "canonical path: " file-path)))
 
 (it "returns a map of the functions in a file with"
     (should (=
              (find-all-functions file-path)
              {:function1 "(defn function1 [param1 param2])"
               :function2 "(defn function2 [param1 param2 param3])"
               :function3 "(defn function3 [])"
               :function4 "(defn function4 [param1])"
               :function5 "(defn function5 [param1 param2])"}))))

(describe
 "parse"

 (it "parses a function from a string"
     (should (=
              (parse fun-str-one)
              {:functions {:function1
                           {:args ["param1" "param2"]
                            :body ""}}})))

 (it "parses a function with a body from a string"
     (should (=
              (parse fun-str-two)
              {:functions {:function2
                           {:args ["param1" "param2"]
                            :body "(* param1 param2)"}}}))))
