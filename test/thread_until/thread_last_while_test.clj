(ns thread-until.thread-last-while-test
  (:require [clojure.test :refer :all]
            [thread-until.core :refer [while->>]]))

(deftest keyword-test
  (testing "Can use a keyword as a predicate."
    (let [expected {:animal :dog
                    :race   "Poodle"
                    :name   "Mr. Teensy"}
          actual (while->> {} (complement :name)
                           (merge {:animal :dog})
                          (merge {:race "Poodle"})
                          (merge {:name "Mr. Teensy"})
                          (merge {:color "white"}))]
      (is (= expected actual)))))