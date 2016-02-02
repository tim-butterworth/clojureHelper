(ns clojurehelper.utilities.parser-spec
  (:require [speclj.core :refer :all]
            [clojurehelper.utilities.parser :refer :all]))

(import java.io.File)

(def file-path (str (. (File. ".") getCanonicalPath) "spec/clojurehelper/utilities/testclojurefile"))

(def file-str
  "(ns clojurehelper.core)

  (defn some-fn [param1 param2]
    (+ param1 param2))

  (defn another-fn [arg1 arg2]
    (some-fn arg1 arg2))"
  )

(defn be-the-same [actual expected]
  (let [the-same (= actual expected)
        ]
    (do
      (if (not the-same)
        (println (str "Expected: [" expected "] but got: [" actual "]")))
      the-same)))

(describe
 "parse"

 (it "parses a files functions"
     (should (be-the-same
              (((parse file-str) :namespace) :functions)
                {
                 :clojurehelper.core.new-file-fn {
                               :params [:clojurehelper.core.param1
                                        :clojurehelper.core.param2]
                               :body [:+
                                      :clojurehelper.core.param1
                                      :clojurehelper.core.param2]
                               }
                 }
                )))

 (it "parses a files namespace"
     (should (be-the-same
              (((parse file-str) :namespace) :name)
              "clojurehelper.core.new-file-fn")))
 )

(describe
 "seperate-file"

 (it "seperates each of the s-expressions"
     (should (be-the-same
              (seperate-file file-str)
              [
               "(ns clojurehelper.core)"
               "(defn some-fn [param1 param2] (+ param1 param2))"
               "(defn another-fn [arg1 arg2] (some-fn arg1 arg2))"
               ]))))

(describe
 "update-current-expression"

 (it "appends the new character to the end of the current expression" 
     (should (be-the-same
              (update-current-expression "hi" (first "a"))
              "hia")))

 (it "does not append the new character to the end of the current expression if it is a newline character"
     (should (be-the-same
              (update-current-expression "hi" (first "\n"))
              "hi")))

 (it "does not append whitespace to the front of a string"
     (should (be-the-same
              (update-current-expression "" (first " "))
              "")))

 (it "does not append whitespace to a string ending in whitespace"
     (should (be-the-same
              (update-current-expression "hi " (first " "))
              "hi "))))

(describe
 "update-stack"

 (it "adds to the stack when the character is an opening character"
     (should (be-the-same
              (update-stack [] (first "("))
              [(first "(")])))

 (it "adds to the end of the stack when the character is an opening character"
     (should (be-the-same
              (update-stack [(first "{")] (first "("))
              [(first "{")
               (first "(")])))

 (it "removes from the stack when the character is a closing character"
     (should (be-the-same
              (update-stack [(first "{") (first "(")] (first ")"))
              [(first "{")])))

  (it "does not remove when the character is a closing character but not a match"
     (should (be-the-same
              (update-stack [(first "{") (first "(")] (first "]"))
              [(first "{")
               (first "(")])))

  (it "does not pop if the stack is empty and the character is nil"
      (should (be-the-same
               (update-stack [] nil)
               [])))
 )

(describe
 "update-current-expression"

 (it "adds the expression to the result list when exiting s-expression"
     (should (be-the-same
              (update-expressions [] "kewl" [] [""])
              ["kewl"])))
 
 (it "does not add the expression to the result list when inside s-expression"
     (should (be-the-same
              (update-expressions [] "kewl" [""] [""])
              [])))
 
 (it "does not add the expression to the result list when outside an s-expression"
     (should (be-the-same
              (update-expressions [] "kewl" [] [])
              []))))

(describe
 "maybe-reset"
 
 (it "resets the expression when the expression has already been added next-expressions"
     (should (be-the-same
              (maybe-reset "something something" [1] [1 2])
              "")))
 
 (it "does not reset the expression when the expression has not been added next-expressions"
     (should (be-the-same
              (maybe-reset "something something" [1] [1])
              "something something"))))
