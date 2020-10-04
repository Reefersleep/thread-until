(ns thread-until.thread-first-while-test
  (:require [clojure.test :refer :all]
            [thread-until.core :refer [while->]]))

(deftest keyword-test
  (testing "Can use a keyword as a predicate."
    (let [expected {:animal :dog
                    :race   "Poodle"
                    :name   "Mr. Teensy"}
          actual (while-> {} (complement :name)
                          (assoc :animal :dog)
                          (assoc :race "Poodle")
                          (assoc :name "Mr. Teensy")
                          (assoc :color "white"))]
      (is (= expected actual)))))