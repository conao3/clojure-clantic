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

(t/deftest validate-nested-test
  (t/testing "nested map"
    (t/is (= {:user {:name "Alice" :age 30}}
             (c/validate {:user {:name :string :age :int}}
                         {:user {:name "Alice" :age 30}}))))

  (t/testing "nested map with extra keys removed"
    (t/is (= {:user {:name "Alice"}}
             (c/validate {:user {:name :string}}
                         {:user {:name "Alice" :age 30}}))))

  (t/testing "nested map validation error"
    (let [ex (try
               (c/validate {:user {:name :string :age :int}}
                           {:user {:name "Alice" :age "30"}})
               (catch Exception e e))]
      (t/is (= {:user {:age ["should be an integer"]}}
               (:errors (ex-data ex))))))

  (t/testing "deeply nested map"
    (t/is (= {:a {:b {:c 42}}}
             (c/validate {:a {:b {:c :int}}}
                         {:a {:b {:c 42}}})))))

(t/deftest validate-vector-test
  (t/testing "vector of primitives"
    (t/is (= {:ids [1 2 3]}
             (c/validate {:ids [:int]}
                         {:ids [1 2 3]}))))

  (t/testing "empty vector"
    (t/is (= {:ids []}
             (c/validate {:ids [:int]}
                         {:ids []}))))

  (t/testing "vector validation error"
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:ids [:int]}
                        {:ids [1 "two" 3]}))))

  (t/testing "vector of maps"
    (t/is (= {:users [{:name "Alice"} {:name "Bob"}]}
             (c/validate {:users [{:name :string}]}
                         {:users [{:name "Alice"} {:name "Bob"}]}))))

  (t/testing "vector of maps with extra keys removed"
    (t/is (= {:users [{:name "Alice"} {:name "Bob"}]}
             (c/validate {:users [{:name :string}]}
                         {:users [{:name "Alice" :age 30} {:name "Bob" :extra "data"}]}))))

  (t/testing "vector of maps validation error"
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:users [{:name :string :age :int}]}
                        {:users [{:name "Alice" :age "30"}]}))))

  (t/testing "nested vector"
    (t/is (= {:matrix [[1 2] [3 4]]}
             (c/validate {:matrix [[:int]]}
                         {:matrix [[1 2] [3 4]]})))))
