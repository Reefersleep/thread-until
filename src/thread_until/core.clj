(ns thread-until.core)

(defmacro until->
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

(defmacro until->>
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

(defmacro while->
  "Threads initial-val through forms like ->.
  Stops evaluating forms, like some->, once
  (predicate-fn threaded-value) returns falsy."
  [inital-val predicate-fn & forms]
  (let [predicated-forms (map (fn [form#]
                                `((fn [[y# keep-going?#]]
                                    (if (and keep-going?# ;;This prevents redundant evaluations of predicate-fn.
                                             (~predicate-fn y#))
                                      [(-> y# ~form#) true]
                                      [y# nil]))))
                              forms)]
    `(-> [~inital-val true]
         ~@predicated-forms
         first)))

(defmacro while->>
  "Threads initial-val through forms like ->>.
  Stops evaluating forms, like some->>, once
  (predicate-fn threaded-value) returns falsy."
  [inital-val predicate-fn & forms]
  (let [predicated-forms (map (fn [form#]
                                `((fn [[y# keep-going?#]]
                                    (if (and keep-going?# ;;This prevents redundant evaluations of predicate-fn.
                                             (~predicate-fn y#))
                                      [(->> y# ~form#) true]
                                      [y# nil]))))
                              forms)]
    `(-> [~inital-val true]
         ~@predicated-forms
         first)))