(ns thread-until.thread-first-until-test
  (:require [#?@(:clj [clojure.test :refer]
                 :cljs [cljs.test :refer-macros]) [deftest testing is]]
            [thread-until.core :refer [until->]]))

(deftest keyword-test
  (testing "Can use a keyword as a predicate."
    (let [expected {:animal :dog
                    :race   "Poodle"
                    :name   "Mr. Teensy"}
          actual   (until-> {} :name
                            (assoc :animal :dog)
                            (assoc :race "Poodle")
                            (assoc :name "Mr. Teensy")
                            (assoc :color "White"))]
      (is (= expected actual)))))

(deftest inline-fn-test
  (testing "Can use an inline fn as a predicate."
    (let [expected {:bears ["Papa" "Mama" "Baby"]}
          actual   (until-> {} (fn [x]
                                 (= 3 (count (:bears x))))
                            (assoc :bears ["Papa"])
                            (update :bears conj "Mama")
                            (update :bears conj "Baby")     ;;Three bears is enough; let's close the door.
                            (assoc :intruder "Goldilocks")
                            (update :bears conj "Uncle"))]
      (is (= expected actual)))))

(deftest comp-test
  (testing "Can use a comp'ed fn as a predicate."
    (let [expected {:lovers ["Edward" "Bella"]}
          actual   (until-> {} (comp #(= % 2) count :lovers)
                            (assoc :lovers ["Edward"])
                            (update :lovers conj "Bella")   ;;Two's company, three's a crowd
                            (update :lovers conj "Jacob"))]
      (is (= expected actual)))))

(deftest no-forms-test
  (testing "Works even when there are no operations."
    (let [expected {}
          actual   (until-> {} :something)]
      (is (= expected actual)))))

(deftest var-test
  (testing "Can use local vars."
    (let [take-exams (fn [degrees exams]
                       (+ degrees exams))
          expected   {:name      "John"
                      :degrees   5
                      :graduated true}
          actual     (until-> {:name    "John"
                               :degrees 1} :graduated
                              (update :degrees take-exams 4)
                              (assoc :graduated true)
                              (update :degrees take-exams 7))] ;;No need to overdo it - John graduated already!
      (is (= expected actual)))))

(deftest closure-test
  (testing "Can close over vars."
    (let [supply     #(update % :boxes conj "Apples")       ;;This is closed over, and will be used.
          load-boxes #(until-> {:boxes []} :ready-for-the-market?
                               supply                       ;;Here, we close over the var.
                               (assoc :ready-for-the-market? true)
                               supply)                      ;;(And here)
          supply     #(update % :boxes conj "Oranges")      ;;This new var with the same name will not be used.
          expected   {:ready-for-the-market? true
                      :boxes                 ["Apples"]}
          actual     (load-boxes)]
      (is (= expected actual)))))

(deftest participation-test
  (testing "Can participate in a -> threading."
    (let [expected {:name     "John"
                    :status   :prince
                    :finances :decent
                    :flaws    ["Unlawful" "Childish"]}
          actual   (-> {:name  "John"
                        :flaws []}
                       (assoc :status :prince)
                       (until-> (comp seq :flaws)
                                (assoc :finances :decent)
                                (update :flaws into ["Unlawful" "Childish"])
                                (assoc :status :king)       ;;John will never be king with those flaws.
                                (assoc :finances :rich)))]
      (is (= expected actual)))))