(ns thread-until.thread-last-until-test
  (:require [clojure.test :refer :all]
            [thread-until.core :refer [->>until]]))

(deftest keyword-test
  (testing "Can use a keyword as a predicate."
    (let [expected {:animal :dog
                    :race   "Poodle"
                    :name   "Mr. Teensy"}
          actual (->>until {} :name
                          (merge {:animal :dog})
                          (merge {:race "Poodle"})
                          (merge {:name "Mr. Teensy"})
                          (merge {:color "white"}))]
      (is (= expected actual)))))

(deftest fn-test
  (testing "Can use an fn as a predicate."
    (let [expected {:uncle "Donald"
                    :nephews ["rip" "rap" "rup"]}
          actual (->>until {} (fn [x]
                               (-> x
                                   :nephews
                                   count
                                   (> 2)))
                          (merge {:uncle "Donald"})
                          (merge {:nephews ["rip" "rap" "rup"]})
                          (merge {:grandma "Duck"}))]
      (is (= expected actual)))))

(deftest comp-test
  (testing "Can use a comp'ed fn as a predicate."
    (let [expected {:nephews ["rip" "rap" "rup" "rop"]}
          actual (->>until {} (comp #(>= % 3) count :nephews)
                           (merge {:nephews ["rip" "rap" "rup" "rop"]})
                           (merge {:scrooge :mcduck}))]
      (is (= expected actual)))))

(deftest no-forms-test
  (testing "Works even when there are no operations."
    (let [expected {}
          actual (->>until {} :something)]
      (is (= expected actual)))))

(deftest var-test
  (testing "Can use local vars"
    (let [my-map {:something true
                  :num       3}
          wont-reach {:something false
                      :num       1000000}
          expected {:something true
                    :num       3}
          actual (->>until {} :something
                          (merge my-map)
                          (merge wont-reach))]
      (is (= expected actual)))))

(deftest closure-test
  (testing "Can close over vars"
    (let [my-num 8
          deferred #(->>until '() (comp (partial = 2) count)
                              (cons 7)
                              (cons my-num)
                              (cons 9))
          my-num 0
          expected '(8 7)
          actual (deferred)]
      (is (= expected actual)))))

(deftest participation-test
  (testing "Can participate in a -> threading."
    (let [expected {:errors ["That"]}
          actual
          (-> {}
              (assoc :errors ["That"])
              (->>until :errors
                       (merge {:unreachable "This"})))]
      (is (= expected actual)))))