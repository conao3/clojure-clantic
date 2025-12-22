(ns conao3.clantic-test
  (:require
   [clojure.test :as t]
   [conao3.clantic :as c]))

(t/deftest validate-test
  (t/testing "returns value for valid data"
    (t/is (= {:name "Alice" :age 30}
             (c/validate {:name :string :age :int}
                         {:name "Alice" :age 30}))))

  (t/testing "throws for invalid data"
    (t/is (thrown-with-msg? clojure.lang.ExceptionInfo #"Validation failed"
            (c/validate {:name :string :age :int}
                        {:name "Alice" :age "30"})))
    (let [ex (try
               (c/validate {:name :string :age :int}
                           {:name 123 :age "30"})
               (catch Exception e e))]
      (t/is (= {:name ["should be a string"] :age ["should be an integer"]}
               (:errors (ex-data ex))))))

  (t/testing "missing keys"
    (let [ex (try
               (c/validate {:name :string :age :int}
                           {:name "Alice"})
               (catch Exception e e))]
      (t/is (= {:age ["missing required key"]}
               (:errors (ex-data ex))))))

  (t/testing "more keys"
    (t/is (= {:a 42}
             (c/validate {:a :int} {:a 42 :b 2})))))

(t/deftest model-validate-types-test
  (t/testing ":string"
    (t/is (= {:v "hello"} (c/validate {:v :string} {:v "hello"})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :string} {:v 123}))))

  (t/testing ":int"
    (t/is (= {:v 42} (c/validate {:v :int} {:v 42})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :int} {:v "42"}))))

  (t/testing ":double"
    (t/is (= {:v 3.14} (c/validate {:v :double} {:v 3.14})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :double} {:v "3.14"}))))

  (t/testing ":boolean"
    (t/is (= {:v true} (c/validate {:v :boolean} {:v true})))
    (t/is (= {:v false} (c/validate {:v :boolean} {:v false})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :boolean} {:v "true"}))))

  (t/testing ":keyword"
    (t/is (= {:v :foo} (c/validate {:v :keyword} {:v :foo})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :keyword} {:v "foo"}))))

  (t/testing ":symbol"
    (t/is (= {:v 'foo} (c/validate {:v :symbol} {:v 'foo})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :symbol} {:v "foo"}))))

  (t/testing ":uuid"
    (let [uuid (random-uuid)]
      (t/is (= {:v uuid} (c/validate {:v :uuid} {:v uuid}))))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :uuid} {:v "not-a-uuid"}))))

  (t/testing ":nil"
    (t/is (= {:v nil} (c/validate {:v :nil} {:v nil})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :nil} {:v "nil"})))))
