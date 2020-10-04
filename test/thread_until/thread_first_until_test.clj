(ns thread-until.thread-first-until-test
  (:require [clojure.test :refer :all]
            [thread-until.core :refer [until->]]))

(deftest keyword-test
  (testing "Can use a keyword as a predicate."
    (let [expected {:animal :dog
                    :race   "Poodle"
                    :name   "Mr. Teensy"}
          actual (until-> {} :name
                          (assoc :animal :dog)
                          (assoc :race "Poodle")
                          (assoc :name "Mr. Teensy")
                          (assoc :color "white"))]
      (is (= expected actual)))))

(deftest fn-test
  (testing "Can use an fn as a predicate."
    (let [expected {:nephews ["rip" "rap" "rup"]}
          actual (until-> {} (fn [x]
                               (-> x
                                   :nephews
                                   count
                                   (> 2)))
                          (assoc :nephews ["rip"])
                          (update :nephews conj "rap")
                          (update :nephews conj "rup")
                          (update :nephews conj "rop"))]
      (is (= expected actual)))))

(deftest comp-test
  (testing "Can use a comp'ed fn as a predicate."
    (let [expected {:nephews ["rip" "rap" "rup" "rop"]}
          actual (until-> {} (comp #(>= % 3) count :nephews)
                          (assoc :nephews ["rip"])
                          (update :nephews conj "rap")
                          (update :nephews concat ["rup" "rop"])
                          (update :nephews conj "ryp"))]
      (is (= expected actual)))))

(deftest no-forms-test
  (testing "Works even when there are no operations."
    (let [expected {}
          actual (until-> {} :something)]
      (is (= expected actual)))))

(deftest var-test
  (testing "Can use local vars"
    (let [my-fn #(* % 3)
          expected {:something true
                    :num       3}
          actual (until-> {:num 1} :something
                          (update :num my-fn)
                          (assoc :something true)
                          (update :num my-fn))]
      (is (= expected actual)))))

(deftest closure-test
  (testing "Can close over vars"
    (let [my-fn #(* % 3)
          deferred #(until-> {:num 1} :something
                             (update :num my-fn)
                             (assoc :something true)
                             (update :num my-fn))
          my-fn #(* % 2)
          expected {:something true
                    :num       3}
          actual (deferred)]
      (is (= expected actual)))))

(deftest participation-test
  (testing "Can participate in a -> threading."
    (let [expected {:errors ["That"]}
          actual
          (-> {}
              (assoc :errors ["That"])
              (until-> :errors
                       (assoc :unreachable "This")))]
      (is (= expected actual)))))