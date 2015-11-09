(ns clojurehelper.io.fileio)

(import java.io.File)
(import java.io.FileReader)
(import java.io.BufferedReader)
(import java.io.FileWriter)
(import java.io.BufferedWriter)

(defn create-new-file [file-path]
  )

(defn readerIn [reader] 
  (loop [accume []] 
    (let [line (. reader readLine)] 
      (if (= nil line) 
        accume
        (recur (conj accume line))))))
