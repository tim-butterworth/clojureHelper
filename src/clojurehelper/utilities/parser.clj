(ns clojurehelper.utilities.parser
  (:require [clojurehelper.utilities.parse-namespace :as namespace]))

(defn get-first-chars [lst]
  (map
   (fn [str] (first str))
   lst))

(def closing
  (zipmap
   (get-first-chars [")" "]" "}"])
   (get-first-chars ["(" "[" "{"])))

(def opening
  (set (vals closing)))

(defn update-stack [stack c]
  (if (contains? opening c)
    (conj stack c)
    (let [close-char (closing c)
          stack-top (last stack)]
      (if (and
           (not (= close-char nil))
           (= close-char stack-top))
        (pop stack)
        stack))))

(defn- should-retain-character [value character]
  (and
   (not (= character (first "\n")))
   (not
    (and
     (= value "")
     (= character (first " "))))
   (not
    (and
     (= (last value) (first " "))
     (= character (first " "))))))

(defn update-current-expression [value character]
  (if (should-retain-character value character)
    (str value character)
    value))

(defn update-expressions [expressions expression current-stack last-stack]
  (let [exiting-expression (and (= 0 (count current-stack)) (= 1 (count last-stack)))]
    (if exiting-expression
      (conj expressions expression)
      expressions)))

(defn maybe-reset [expression expressions next-expressions]
  (if (> (count next-expressions) (count expressions))
    ""
    expression))

(defn seperate-file [clojure-file-as-string]
  (loop [str clojure-file-as-string
         current-expression ""
         stack []
         expressions []]
    (if (not (empty? str))
      (let [c (first str)
            updated-stack (update-stack stack c)
            updated-current-expression (update-current-expression current-expression c)
            updated-expressions (update-expressions expressions updated-current-expression updated-stack stack)
            maybe-reset-expression (maybe-reset updated-current-expression expressions updated-expressions)]
          (recur (rest str) maybe-reset-expression updated-stack updated-expressions))
      expressions)))

(defn parse [clojure-file]
  (let [seperated-file (seperate-file clojure-file)
        namespace (do
                    (println (str "FIRST SEPERATED-FILE: -> " (first seperated-file)))
                    (namespace/parse (first seperated-file)))]
    {
     :namespace
     {
      :functions {
                  }
      }
     }))

