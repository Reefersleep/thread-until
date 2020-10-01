(ns thread-until.core)

(defmacro ->until1 [predicate-fn x & forms]
  (let [result (eval x)
        realized-predicate (eval predicate-fn)]
    (if (realized-predicate result)
      result
      (if forms
        (let [form (first forms)
              threaded (if (seq? form)
                         (list `-> result form)
                         (list form result))]
          `(->until ~predicate-fn ~threaded ~@(next forms)))
        result))))

(defmacro ->until2 [predicate-fn x & forms]
  `(let [forms# '~forms
         result# ~x
         realized-predicate# ~predicate-fn]
     (if (realized-predicate# result#)
       result#
       (if forms#
         (let [form# (first forms#)]
           (-> result# form#))
         result#))))

(defmacro ->until3 [predicate-fn x & forms]
  (let [pred (eval predicate-fn)]
    (loop [x x
           forms forms]
      (let [result (eval x)]
        (cond
          (pred result)
          result

          forms
          (let [form (first forms)
                threaded (list `-> result form)]
            (recur threaded (next forms)))

          :else
          result)))))

;;Works for the tests, but huge expansion!
;;Goes crazy with more than 5 forms or so.
;;E.g.
(comment (macroexpand '(->until :something
                                {:num 1}
                                (update :num my-fn)
                                (assoc :something true)
                                (update :num my-fn)
                                (update :num my-fn)
                                (assoc :something true)
                                (update :num my-fn)
                                )))
(defmacro ->until5 [predicate-fn x & forms]
  (loop [x x
         forms forms]
    (if forms
      (let [form (first forms)
            threaded (list `if (list predicate-fn x)
                           x
                           (list `-> x form))]
        (recur threaded (next forms)))
      x)))

;;Pretty good, but evaluates predicate-fn for every form
;;Maybe take inspiration from some->
(defmacro ->until10 [predicate-fn x & forms]
  `(-> ~x ~@(map (fn [form#]
                   `((fn [y#]
                       (if (~predicate-fn y#)
                         y#
                         (-> y# ~form#)))))
                 forms)))                                   ;;Does this make sense?


(defmacro ->until
  "Like ->, but stops evaluating forms once
  (predicate-fn threaded-value) returns truthy."
  [predicate-fn x & forms]
  (let [predicated-forms (map (fn [form#]
                                `((fn [[y# done?#]]
                                    (if (or done?#          ;;This prevents redundant evaluations of predicate-fn.
                                            (~predicate-fn y#))
                                      [y# true]
                                      [(-> y# ~form#) nil]))))
                              forms)]
    `(-> [~x nil]
         ~@predicated-forms
         first)))

(defmacro ->until6 [predicate-fn x & forms]
  `(loop [x# ~x
          pred# ~predicate-fn
          forms# forms]
     (if (pred# x#)
       x#
       (if forms#
         (let [form# (first forms#)
               threaded# (-> x# form#)]
           (recur threaded# pred# (next forms#)))
         x#))))

;;Works, expansion is not huge, but a little obtuse code
;;Can't close over vars!
(defmacro ->until7 [predicate-fn x & forms]
  (let [quoted-forms (some->> forms
                              (map (fn [form]
                                     (list `quote form)))
                              ;;Is there a problem in doing regular old quoting rather than syntax quoting?
                              ;;What if the macro is used in an fn which is transported out of its original context before evaluation?
                              (cons `list))]
    `(loop [x# ~x
            pred# ~predicate-fn
            forms# ~quoted-forms]
       (if (pred# x#)
         x#
         (if forms#
           (let [form# (first forms#)
                 threaded# (eval (list `-> x# form#))]
             (recur threaded# pred# (next forms#)))
           x#)))))