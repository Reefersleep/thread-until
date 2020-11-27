(ns thread-until.thread-last-while-test
  (:require [#?@(:clj [clojure.test :refer]
                 :cljs [cljs.test :refer-macros]) [deftest testing is]]
            [thread-until.core :refer [while->>]]))

(deftest while-test
  (testing "Stops actions once predicate is met."
    (let [expected '(0 2 4 6)
          actual (while->> (range 10) (comp #(> % 5) count)
                           (take 7)
                           (filter even?)
                           (take 2))]                       ;;We won't reach this.
      (is (= expected actual)))))

(deftest no-forms-test
  (testing "Works even when there are no operations."
    (let [expected {}
          actual   (while->> {} :something)]
      (is (= expected actual)))))