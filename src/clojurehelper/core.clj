(ns clojurehelper.core
  (:require [clojurehelper.io.fileio :refer :all]))

(defn new-file-fn [project-root]
  (fn ([file-path]
       (create-new-project-file project-root file-path))))

(defn clojure-helper [project-root]
  {:new-file (new-file-fn project-root)
   :project-root project-root})
