(ns clojurehelper.io.fileio)

(import java.io.File)
(import java.io.FileReader)
(import java.io.BufferedReader)
(import java.io.FileWriter)
(import java.io.BufferedWriter)

(defn path-to-list [root]
  (filter
   #(not (empty? %))
   (clojure.string/split root #"/")))

(defn make-namespace [root-lst ns-lst]
  (do
    (println root-lst)
    (println ns-lst)
    (str
     "(ns "
     (clojure.string/join "." ns-lst)
     ")")))

(defn create-new-file [project-root file-path]
  (let [path-lst (path-to-list project-root)
        full-path-lst (path-to-list file-path)
        name-space (make-namespace path-lst full-path-lst)]))

(defn readerIn [reader] 
  (loop [accume []] 
    (let [line (. reader readLine)] 
      (if (= nil line) 
        accume
        (recur (conj accume line))))))
