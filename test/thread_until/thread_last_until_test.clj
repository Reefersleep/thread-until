(ns thread-until.thread-last-until-test
  (:require [clojure.test :refer :all]
            [thread-until.core :refer [until->>]]))

(deftest anonymous-fn-test
  (testing "Can use an anonymous fn as a predicate."
    (let [expected [{:name "Fido"
                     :type :mammal}
                    {:name "Lucy"
                     :type :mammal}]
          animals  [{:name "Fido"
                     :type :mammal}
                    {:name "Lucy"
                     :type :mammal}
                    {:name "George"
                     :type :reptile}
                    {:name "Max"
                     :type :bird}]
          actual   (until->> animals #(every? (comp #{:mammal} :type) %)
                             butlast
                             (remove (comp #{"George"} :name))
                             (concat [{:name "Abraham"
                                       :type :amphibian}]))]
      (is (= expected actual)))))

(deftest no-forms-test
  (testing "Works even when there are no operations."
    (let [expected {}
          actual   (until->> {} :something)]
      (is (= expected actual)))))

(deftest closure-test
  (testing "Can close over vars."
    (let [my-num   8                                        ;;Closed over and will be used.
          deferred #(until->> '() (comp (partial = 2) count)
                              (cons 7)
                              (cons my-num)                 ;;We close over it here.
                              (cons 9))
          my-num   0                                        ;;Won't be used.
          expected '(8 7)
          actual   (deferred)]
      (is (= expected actual)))))

(deftest participation-test
  (testing "Can participate in a -> threading."
    (let [expected '({:name "Jennifer"}
                     {:name "The Son"}
                     {:name "The Father"})
          actual   (-> {}
                       (assoc :name "The Father")
                       vector
                       (until->> #(some (comp #{"Jennifer"} :name) %)
                                 (cons {:name "The Son"})
                                 (cons {:name "Jennifer"})
                                 (cons {:name "Holy Ghost"})))]
      (is (= expected actual)))))