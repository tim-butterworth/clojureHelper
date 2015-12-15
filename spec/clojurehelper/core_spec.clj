(ns clojurehelper.core_spec
  (:require [speclj.core :refer :all]
            [clojurehelper.core :refer :all]
            [clojurehelper.io.fileio :refer :all]))

(import java.io.File)
(import java.io.FileReader)
(import java.io.BufferedReader)

(def mocks 
  (atom {}))

(def original-fns
  (atom {}))

(defn reset-mocks []
  (do
    (println "done resetting the mocks")
    (doseq [entry @original-fns]
      (let [fn-var (entry 0)
             original-fn (entry 1)]
         (.bindRoot fn-var original-fn)))))

(defn save-original [fn-var]
  (do
    (println "done saving the original")
    (swap!
     original-fns
     (fn [mp]
       (assoc mp
              fn-var
              (. fn-var getRawRoot))))))

(defn local-bind [fn-var fun]
  (.bindRoot 
   fn-var
   fun))

(defmacro mock-fn [fn-name] 
  `(let [fn-var# ~(list `var fn-name)]
     (do
       (save-original fn-var#)
       (local-bind
        fn-var#
        (fn
          [& n#] 
          (swap! mocks 
                 (fn [mp#] 
                   (do
                     (println (str "Calling a mock... " ~(keyword fn-name)))
                     (assoc mp# ~(keyword fn-name) n#)))))))))

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

(let [helper (clojure-helper project-root-dir)]
    (describe 
     "create-new-file"
     
     (it "retains the project root directory"
         (should (= (helper :project-root) project-root-dir)))

     (describe "new-file"
               (before
                (def file-path "path-to-file")
                (mock-fn create-new-project-file)
                ((helper :new-file) file-path))

               (after
                (reset-mocks))
               
               (it "calls create file with the correct path"
                   (should (have-called create-new-project-file
                                        (with-args project-root-dir file-path)))))
     )
  )

(run-specs)
