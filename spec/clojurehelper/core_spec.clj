(ns clojurehelper.core_spec
  (:require [speclj.core :refer :all]
            [clojurehelper.spec-helper :refer :all]
            [clojurehelper.core :refer :all]
            [clojurehelper.io.fileio :refer :all]))

(import java.io.File)
(import java.io.FileReader)
(import java.io.BufferedReader)

(def project-root-dir
  (str (.getCanonicalPath (File. "")) "/spec/test-project"))

(let [helper (clojure-helper project-root-dir)]
    (describe 
     "create-new-file"
     
     (it "retains the project root directory"
         (should (= (helper :project-root) project-root-dir)))

     (describe "new-file"
               (before
                (def file-path "path-to-file")
                (mock-fn create-new-project-file do-nothing)
                ((helper :new-file) file-path))

               (after
                (reset-mocks))
               
               (it "calls create file with the correct path"
                   (should (have-called create-new-project-file
                                        (with-args project-root-dir file-path)))))
     )
  )

(run-specs)
