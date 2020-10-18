(ns thread-until.thread-first-while-test
  (:require [clojure.test :refer :all]
            [thread-until.core :refer [while->]]))

(deftest while-test
  (testing "Stop actions once predicate is met."
    (let [withdraw! (fn [account amount]
                      (update account :credit - amount))
          expected  {:name   "John"
                     :credit 400}
          actual    (while-> {:name   "John"
                              :credit 600} (comp #(>= % 500) :credit)
                             (withdraw! 100)
                             (withdraw! 100)
                             (withdraw! 400))]              ;;Sorry, you'll have to make do with the 200.
      (is (= expected actual)))))

(deftest no-forms-test
  (testing "Works even when there are no operations."
    (let [expected {}
          actual   (while-> {} :something)]
      (is (= expected actual)))))

(deftest participation-test
  (testing "Can participate in a -> threading."
    (let [expected '("The Son"
                      "Jennifer"
                      "The Holy Ghost")
          actual   (-> "The"
                       (str " Father")
                       vector
                       (while-> #(some #{"The Father"} %)
                                (conj "The Son")
                                (conj "Jennifer")
                                (conj "The Holy Ghost")
                                rest
                                (conj "Beelzebub")          ;;The Father is not present to approve these guys.
                                (conj "John")))]
      (is (= expected actual)))))