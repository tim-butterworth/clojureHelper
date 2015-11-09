(ns clojurehelper.core
  (:require [clojurehelper.io.fileio :refer :all])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def project-root
  (atom ""))

(defn set-project-root-dir [path]
  (swap! project-root (fn [n] path)))

(defn new-file [file-path]
  (do
    (println file-path)
    (create-new-file (str @project-root "/" file-path))))


