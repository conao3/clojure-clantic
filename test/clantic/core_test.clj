(ns clantic.core-test
  (:require
   [clojure.test :as t]
   [clantic.core :as c]))

(t/deftest model-validate-test
  (t/testing "returns value for valid data"
    (t/is (= {:name "Alice" :age 30}
             (c/model-validate {:name :string :age :int}
                               {:name "Alice" :age 30}))))

  (t/testing "throws for invalid data"
    (t/is (thrown-with-msg? clojure.lang.ExceptionInfo #"Validation failed"
            (c/model-validate {:name :string :age :int}
                              {:name "Alice" :age "30"})))
    (let [ex (try
               (c/model-validate {:name :string :age :int}
                                 {:name 123 :age "30"})
               (catch Exception e e))]
      (t/is (= {:name ["should be a string"] :age ["should be an integer"]}
               (:errors (ex-data ex))))))

  (t/testing "missing keys"
    (let [ex (try
               (c/model-validate {:name :string :age :int}
                                 {:name "Alice"})
               (catch Exception e e))]
      (t/is (= {:age ["missing required key"]}
               (:errors (ex-data ex)))))))
