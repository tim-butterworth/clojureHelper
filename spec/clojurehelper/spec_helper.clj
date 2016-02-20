(ns clojurehelper.spec-helper)

(defn be-the-same [actual expected]
  (let [the-same (= actual expected)]
    (do
      (if (not the-same)
        (println (str "Expected: [" expected "] but got: [" actual "]")))
      the-same)))

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

(defmacro mock-fn [fn-name fun] 
  `(let [fn-var# ~(list `var fn-name)]
     (do
       (save-original fn-var#)
       (local-bind
        fn-var#
        (fn [& n#] 
          (do
            (swap! mocks 
                   (fn [mp#] 
                     (do
                       (println (str "Calling a mock... " ~(keyword fn-name)))
                       (assoc mp# ~(keyword fn-name) n#))))
            (~fun n#)))))))

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

(def do-nothing (fn [& args]))
