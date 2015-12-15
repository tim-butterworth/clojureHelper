(ns clojurehelper.utilities.parser)

(defn parse [clojure-file])

(defn find-all-functions [file-path]
  (let [file-string (slurp file-path)]
    (parse file-string)))


;(defn function1 [param1 param2])

;(defn function2 [param1 param2 param3])
;(defn function3 [])
;(defn function4 [param1])(defn function5 [param1 param2])
