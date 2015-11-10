(ns clojurehelper.core
  (:require [clojurehelper.io.fileio :refer :all])
  (:gen-class))

(defn -main
  [& args]
  (println "Hello, World!"))

(def project-root
  (atom ""))

(defn set-project-root-dir [path]
  (swap! project-root (fn [n] path)))

(defn new-file [file-path]
  (create-new-file @project-root file-path))
