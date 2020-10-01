(ns thread-until.core)

(defmacro ->until
  "Threads initial-val through forms like ->.
  Stops evaluating forms, like some->, once
  (predicate-fn threaded-value) returns truthy."
  [inital-val predicate-fn & forms]
  (let [predicated-forms (map (fn [form#]
                                `((fn [[y# done?#]]
                                    (if (or done?#          ;;This prevents redundant evaluations of predicate-fn.
                                            (~predicate-fn y#))
                                      [y# true]
                                      [(-> y# ~form#) nil]))))
                              forms)]
    `(-> [~inital-val nil]
         ~@predicated-forms
         first)))

(defmacro ->>until
  "Threads initial-val through forms like ->>.
  Stops evaluating forms, like some->>, once
  (predicate-fn threaded-value) returns truthy."
  [inital-val predicate-fn & forms]
  (let [predicated-forms (map (fn [form#]
                                `((fn [[y# done?#]]
                                    (if (or done?#          ;;This prevents redundant evaluations of predicate-fn.
                                            (~predicate-fn y#))
                                      [y# true]
                                      [(->> y# ~form#) nil]))))
                              forms)]
    `(-> [~inital-val nil]
         ~@predicated-forms
         first)))