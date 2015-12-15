(ns clojurehelper.io.fileio-spec
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
  (str (. (File. ".") getCanonicalPath) "/test-resources/clojurehelper/test-project"))

(defn delete-recursively [file]
  (let [dir-contents (. file listFiles)]
    (do
      (doseq [dir-file dir-contents]
        (delete-recursively dir-file))
      (. file delete))))

(defn read-file-lines [file-path]
  (clojure.string/split
   (slurp file-path)
   #"\n"))

(let [test-path (str file-path "/dir/anotherdir/finaldir/")
      test-path-list (filter
                      (fn [n]
                        (not (= n "")))
                      (clojure.string/split test-path #"/"))
      dir-path (make-dir-recursively test-path-list)]

  (describe
   "make-dir-recursively"

   (before (make-dir-recursively test-path-list))
   
   (after
    (delete-recursively (File. file-path)))

   (it "returns the full path of the created directory"
       (should (be-same test-path
                        dir-path)))
   
   (it "creates a directory if it does not already exist"
       (should (file-exists (str file-path "/dir/anotherdir/finaldir"))))))

(def extension "/test-project/somedir")
(def clojure-file (str "/src/" extension "/file.clj"))
(def spec-file (str "/spec/" extension "/file_spec.clj"))

(describe
 "create-new-project-file"
 (before
  (create-new-project-file file-path "somedir/file.clj"))

 (after
  (delete-recursively (File. file-path)))
 
 (it "creates a new directory"
     (should (file-exists (str file-path "/src/test-project/somedir"))))

 (it "creates a new file"
     (should (file-exists (str file-path clojure-file))))

 (it "writes the correct file namespace"
     (should (be-same "(ns test-project.somedir.file)"
              (first
               (read-file-lines (str file-path clojure-file))))))

 (it "creates a new spec file"
     (should (file-exists (str file-path spec-file))))

 (it "writes the correct spec namespace"
     (should (be-same "(ns test-project.somedir.file-spec)"
                      (first
                       (read-file-lines (str file-path spec-file))))))
 )

(describe
 "path-to-list"
 (it "converts a file path to a list of directories"
     (should (be-same ["hi" "how" "are" "you"] (path-to-list "/hi/how/are/you"))))
 (it "converts an empty string into an empty list"
     (should (be-same [] (path-to-list "")))))

(describe
 "clojure-project-file"

 (it "makes a clojure project file path based on the project root and the new file relative location"
     (should (be-same
              ["project" "root" "project-name" "src" "project-name" "relative" "location" "file.clj"]
              (clojure-project-file ["project" "root" "project-name"] ["relative" "location" "file.clj"])))))

(describe
 "to-spec-file"

 (it "generates the spec name for a file"
     (should (be-same
              "file_spec.clj"
              (to-spec-file "file.clj")))))
(describe
 "clojure-spec-file"

 (it "makes a clojure spec file path based on the project root and the new file relative location"
     (should (be-same
              ["project" "root" "project-name" "spec" "project-name" "relative" "location" "file_spec.clj"]
              (clojure-spec-file ["project" "root" "project-name"] ["relative" "location" "file.clj"])))))

(describe
 "make-namespace"
 (it "makes the clojure file namespace based on the root path and full path"
     (should (be-same
              "(ns path.base.path.something.something)"
              (make-namespace ["base" "path"] ["base" "path" "something" "something"]))))

 (it "makes the clojure file namespace based on the root path and full path, the root ns should be lowercase"
     (should (be-same
              "(ns pathcamelcase.base.path.something.something)"
              (make-namespace ["base" "pathCamelCase"] ["base" "path" "something" "something"])))))

(describe
 "make-spec-namespace"
 (it "makes the clojure spec namespace based on the root path and full path"
     (should (be-same
              "(ns path.base.path.something.something-spec)"
              (make-spec-namespace ["base" "path"] ["base" "path" "something" "something"])))))

(run-specs)
