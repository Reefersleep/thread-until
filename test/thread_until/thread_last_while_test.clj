(ns thread-until.thread-last-while-test
  (:require [clojure.test :refer :all]
            [thread-until.core :refer [while->>]]))

(deftest initial-test
  (testing "Just an initial test."
    (let [expected {:animal :dog
                    :race   "Poodle"
                    :name   "Mr. Teensy"}
          actual (while->> {} (complement :name)
                           (merge {:animal :dog})
                           (merge {:race "Poodle"})
                           (merge {:name "Mr. Teensy"})
                           (merge {:color "white"}))]
      (is (= expected actual)))))