(ns clojurehelper.io.fileio_spec
  (:require [speclj.core :refer :all]
            [clojurehelper.io.fileio :refer :all]))

(import java.io.File)

(defn be-same [expected actual]
  (if (= expected actual)
    true
    (do
      (println (str "Expected: " expected))
      (println (str "Actual: " actual))
      false)))

(defn file-exists [full-path]
  (let [file (File. full-path)]
    (if (. file exists)
      true
      (do
        (println (str "File: " full-path " does not exist"))
        false))))

(def file-path
  (. (File. ".") getCanonicalPath))

(println (str "file-path -> " file-path))

(describe
 "create-new-file"
 (before 
  (create-new-file (str file-path "/test-project") "somedir/file.clj")
  
  (it "creates a new file"
      (should (file-exists (str file-path "/test-project/src/test-project/somedir/file.clj"))))))

(describe
 "path-to-list"
 (it "converts a file path to a list of directories"
     (should (be-same ["hi" "how" "are" "you"] (path-to-list "/hi/how/are/you"))))
 (it "converts an empty string into an empty list"
     (should (be-same [] (path-to-list "")))))

(describe
 "make-namespace"
 (it "makes the clojure file namespace based on the root path and full path"
     (should (be-same
              "(ns something.something)"
              (make-namespace ["base" "path"] ["base" "path" "something" "something"])))))

(run-specs)
