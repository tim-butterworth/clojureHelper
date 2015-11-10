(ns clojurehelper.core_spec
  (:require [speclj.core :refer :all]
            [clojurehelper.core :refer :all]
            [clojurehelper.io.fileio :refer :all]))

(import java.io.File)
(import java.io.FileReader)
(import java.io.BufferedReader)

(def mocks 
  (atom {}))

(defn local-bind [fn-var fun]
  (do
    (.bindRoot 
     fn-var
     fun)))

(defmacro mock-fn [fn-name] 
  `(local-bind
    ~(list `var fn-name)
    (fn
      [& n#] 
      (swap! mocks 
             (fn [mp#] 
               (assoc mp# ~(keyword fn-name) n#))))))

(defmacro have-called [fun & body]
  `(let [fn-key# ~(keyword fun)]
     (~@body (@mocks fn-key#))))

(defn with-args [& args]
  (fn [called-args]
    (if (= args called-args)
      true
      (do
        (println (str "Expected : " args))
        (println (str "Got : " called-args))
        false))))

(def project-root-dir
  (str (.getCanonicalPath (File. "")) "/spec/test-project"))

(describe 
 "create-new-file"
 (before
  
  (set-project-root-dir project-root-dir)
  (def mock-calls (atom {}))
  )
 
 (it "retains the project root directory"
     (should (= @project-root project-root-dir)))

 (describe "new-file"
           (before
            (def file-path "path-to-file")
            (mock-fn create-new-file)
            (new-file file-path)
            )
           (it "calls create file with the correct path"
               (should (have-called create-new-file
                                    (with-args @project-root file-path)))))
 )

(run-specs)
