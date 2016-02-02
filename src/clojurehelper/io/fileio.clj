(ns clojurehelper.io.fileio)

(import java.io.File)
(import java.io.FileWriter)
(import java.io.BufferedWriter)

(defn string-join [token list]
  (clojure.string/join token list))

(defn path-to-list [root]
  (filter
   #(not (empty? %))
   (clojure.string/split root #"/")))

(defn- build-extension-list [root extension-list token]
  (flatten
   [
    root
    token
    (last root)
    extension-list
    ]))

(defn clojure-project-file [project-root file-extension-list]
  (build-extension-list project-root file-extension-list "src"))

(defn to-spec-file [file-name]
  (let [parts (clojure.string/split file-name #"\.")]
    (str (parts 0) "_spec." (parts 1))))

(defn clojure-spec-file [project-root file-extension-list]
  (build-extension-list project-root
                        [
                         (butlast file-extension-list)
                         (to-spec-file (last file-extension-list))
                         ]
                        "spec"))

(defn- build-namespace
  ([root-lst ns-lst]
   (build-namespace root-lst ns-lst ""))
  ([root-lst ns-lst suffix]
   (str
    "(ns "
    (string-join "." [
                      (. (last root-lst) toLowerCase)
                      (string-join "." (butlast ns-lst))
                      (str (first (clojure.string/split (last ns-lst) #"\.")) suffix)
                      ]
                 )
    ")")))

(defn make-namespace [root-lst ns-lst]
  (build-namespace root-lst ns-lst))

(defn make-spec-namespace [root-lst ns-lst]
  (build-namespace root-lst ns-lst "-spec"))

(defn create-dir-if-needed [path]
  (let [file (File. path)]
    (if (not (. file exists))
      (. file mkdir))
    path))

(defn make-dir-recursively
  ([file-path-list]
   (make-dir-recursively "/" file-path-list))
  ([path dir-lst]
   (if (not (empty? dir-lst))
     (make-dir-recursively
      (create-dir-if-needed (str path (first dir-lst) "/"))
      (rest dir-lst))
     path)))

(defn write-file [file-path name-space]
  (try
    (let [file (File. file-path)]
      (with-open [writer (FileWriter. (File. file-path))
                  bufferedWriter (BufferedWriter. writer)]
        (. bufferedWriter write name-space)))
    (catch Exception e
      (println (str "Error writing:::" (. e getMessage))))))

(defn create-new-project-file [project-root file-extension]
  (let [
        root-path-list (path-to-list project-root)
        file-extension-list (path-to-list file-extension)
        name-space (make-namespace root-path-list file-extension-list)
        spec-name-space (make-spec-namespace root-path-list file-extension-list)
        file-path-list (clojure-project-file root-path-list file-extension-list)
        spec-path-list (clojure-spec-file root-path-list file-extension-list)
        spec-directory (butlast spec-path-list)
        directory (butlast file-path-list)
        file-name (last file-path-list)
        ]
    (make-dir-recursively directory)
    (make-dir-recursively spec-directory)
    (write-file (str "/" (clojure.string/join #"/" file-path-list)) name-space)
    (write-file (str "/" (clojure.string/join #"/" spec-path-list)) spec-name-space)))

;(defn readerIn [reader] 
;  (loop [accume []] 
;    (let [line (. reader readLine)] 
;      (if (= nil line) 
;        accume
;       (recur (conj accume line))))))
