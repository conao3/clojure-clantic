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
                        {:name "Alice" :age "not-a-number"}))))

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
    (t/is (= {:v 42} (c/validate {:v :int} {:v "42"})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :int} {:v "not-a-number"}))))

  (t/testing ":double"
    (t/is (= {:v 3.14} (c/validate {:v :double} {:v 3.14})))
    (t/is (= {:v 3.14} (c/validate {:v :double} {:v "3.14"})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :double} {:v "not-a-number"}))))

  (t/testing ":boolean"
    (t/is (= {:v true} (c/validate {:v :boolean} {:v true})))
    (t/is (= {:v false} (c/validate {:v :boolean} {:v false})))
    (t/is (= {:v true} (c/validate {:v :boolean} {:v "true"})))
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v :boolean} {:v "yes"}))))

  (t/testing ":keyword"
    (t/is (= {:v :foo} (c/validate {:v :keyword} {:v :foo})))
    (t/is (= {:v :foo} (c/validate {:v :keyword} {:v "foo"})))
    (t/is (thrown? clojure.lang.ExceptionInfo
                   (c/validate {:v :keyword} {:v nil}))))

  (t/testing ":symbol"
    (t/is (= {:v 'foo} (c/validate {:v :symbol} {:v 'foo})))
    (t/is (= {:v 'foo} (c/validate {:v :symbol} {:v "foo"}))))

  (t/testing ":uuid"
    (let [uuid (random-uuid)]
      (t/is (= {:v uuid} (c/validate {:v :uuid} {:v uuid})))
      (t/is (= {:v uuid} (c/validate {:v :uuid} {:v (str uuid)}))))
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

  (t/testing "nested map with coercion"
    (t/is (= {:user {:name "Alice" :age 30}}
             (c/validate {:user {:name :string :age :int}}
                         {:user {:name "Alice" :age "30"}}))))

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

  (t/testing "vector of maps with coercion"
    (t/is (= {:users [{:name "Alice" :age 30}]}
             (c/validate {:users [{:name :string :age :int}]}
                         {:users [{:name "Alice" :age "30"}]}))))

  (t/testing "nested vector"
    (t/is (= {:matrix [[1 2] [3 4]]}
             (c/validate {:matrix [[:int]]}
                         {:matrix [[1 2] [3 4]]})))))

(t/deftest validate-optional-test
  (t/testing "optional field present"
    (t/is (= {:name "Alice" :age 30}
             (c/validate {:name :string :age [:optional :int]}
                         {:name "Alice" :age 30}))))

  (t/testing "optional field missing"
    (t/is (= {:name "Alice"}
             (c/validate {:name :string :age [:optional :int]}
                         {:name "Alice"}))))

  (t/testing "optional field with nil value"
    (t/is (= {:name "Alice" :age nil}
             (c/validate {:name :string :age [:optional :int]}
                         {:name "Alice" :age nil}))))

  (t/testing "optional field with coercion"
    (t/is (= {:name "Alice" :age 30}
             (c/validate {:name :string :age [:optional :int]}
                         {:name "Alice" :age "30"}))))

  (t/testing "optional nested map"
    (t/is (= {:user {:name "Alice"}}
             (c/validate {:user [:optional {:name :string}]}
                         {:user {:name "Alice"}}))))

  (t/testing "optional nested map missing"
    (t/is (= {}
             (c/validate {:user [:optional {:name :string}]}
                         {}))))

  (t/testing "optional vector"
    (t/is (= {:ids [1 2 3]}
             (c/validate {:ids [:optional [:int]]}
                         {:ids [1 2 3]}))))

  (t/testing "optional vector missing"
    (t/is (= {}
             (c/validate {:ids [:optional [:int]]}
                         {})))))

(t/deftest validate-default-test
  (t/testing "default field present"
    (t/is (= {:name "Alice" :age 30}
             (c/validate {:name :string :age [:default :int 0]}
                         {:name "Alice" :age 30}))))

  (t/testing "default field missing"
    (t/is (= {:name "Alice" :age 0}
             (c/validate {:name :string :age [:default :int 0]}
                         {:name "Alice"}))))

  (t/testing "default field with coercion"
    (t/is (= {:name "Alice" :age 30}
             (c/validate {:name :string :age [:default :int 0]}
                         {:name "Alice" :age "30"}))))

  (t/testing "default with string"
    (t/is (= {:name "Anonymous"}
             (c/validate {:name [:default :string "Anonymous"]}
                         {}))))

  (t/testing "default with nested map"
    (t/is (= {:config {:enabled true}}
             (c/validate {:config [:default {:enabled :boolean} {:enabled false}]}
                         {:config {:enabled true}}))))

  (t/testing "default nested map missing"
    (t/is (= {:config {:enabled false}}
             (c/validate {:config [:default {:enabled :boolean} {:enabled false}]}
                         {}))))

  (t/testing "default with vector"
    (t/is (= {:ids []}
             (c/validate {:ids [:default [:int] []]}
                         {}))))

  (t/testing "default with vector present"
    (t/is (= {:ids [1 2 3]}
             (c/validate {:ids [:default [:int] []]}
                         {:ids [1 2 3]})))))

(t/deftest validate-coerce-test
  (t/testing "coerce string to int"
    (t/is (= {:age 30}
             (c/validate {:age :int}
                         {:age "30"}))))

  (t/testing "coerce int stays int"
    (t/is (= {:age 30}
             (c/validate {:age :int}
                         {:age 30}))))

  (t/testing "coerce string to double"
    (t/is (= {:price 3.14}
             (c/validate {:price :double}
                         {:price "3.14"}))))

  (t/testing "coerce int to double"
    (t/is (= {:price 3.0}
             (c/validate {:price :double}
                         {:price 3}))))

  (t/testing "coerce string to boolean"
    (t/is (= {:enabled true}
             (c/validate {:enabled :boolean}
                         {:enabled "true"})))
    (t/is (= {:enabled false}
             (c/validate {:enabled :boolean}
                         {:enabled "false"}))))

  (t/testing "coerce string to keyword"
    (t/is (= {:status :active}
             (c/validate {:status :keyword}
                         {:status "active"}))))

  (t/testing "coerce string to symbol"
    (t/is (= {:name 'foo}
             (c/validate {:name :symbol}
                         {:name "foo"}))))

  (t/testing "coerce string to uuid"
    (let [uuid (random-uuid)]
      (t/is (= {:id uuid}
               (c/validate {:id :uuid}
                           {:id (str uuid)})))))

  (t/testing "coerce invalid string to int throws"
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:age :int}
                        {:age "not-a-number"}))))

  (t/testing "coerce with optional"
    (t/is (= {:name "Alice"}
             (c/validate {:name :string :age [:optional :int]}
                         {:name "Alice"}))))

  (t/testing "coerce with default"
    (t/is (= {:age 0}
             (c/validate {:age [:default :int 0]}
                         {}))))

  (t/testing "coerce in vector"
    (t/is (= {:ids [1 2 3]}
             (c/validate {:ids [:int]}
                         {:ids ["1" "2" "3"]})))))

(t/deftest validate-union-test
  (t/testing "union with string"
    (t/is (= {:v "hello"}
             (c/validate {:v [:or :string :int]}
                         {:v "hello"}))))

  (t/testing "union with int"
    (t/is (= {:v 42}
             (c/validate {:v [:or :string :int]}
                         {:v 42}))))

  (t/testing "union first match wins"
    (t/is (= {:v "42"}
             (c/validate {:v [:or :string :int]}
                         {:v "42"}))))

  (t/testing "union with coercion (int first)"
    (t/is (= {:v 42}
             (c/validate {:v [:or :int :string]}
                         {:v "42"}))))

  (t/testing "union validation error"
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:v [:or :string :int]}
                        {:v :keyword}))))

  (t/testing "union with nil"
    (t/is (= {:v nil}
             (c/validate {:v [:or :nil :string]}
                         {:v nil}))))

  (t/testing "union in optional"
    (t/is (= {:name "Alice"}
             (c/validate {:name :string :id [:optional [:or :string :int]]}
                         {:name "Alice"}))))

  (t/testing "union in vector"
    (t/is (= {:values ["a" 1 "b" 2]}
             (c/validate {:values [[:or :string :int]]}
                         {:values ["a" 1 "b" 2]})))))

(t/deftest validate-enum-test
  (t/testing "enum with valid string"
    (t/is (= {:status "active"}
             (c/validate {:status [:enum "active" "inactive" "pending"]}
                         {:status "active"}))))

  (t/testing "enum with another valid value"
    (t/is (= {:status "pending"}
             (c/validate {:status [:enum "active" "inactive" "pending"]}
                         {:status "pending"}))))

  (t/testing "enum validation error"
    (t/is (thrown? clojure.lang.ExceptionInfo
            (c/validate {:status [:enum "active" "inactive"]}
                        {:status "unknown"}))))

  (t/testing "enum with keywords"
    (t/is (= {:type :admin}
             (c/validate {:type [:enum :admin :user :guest]}
                         {:type :admin}))))

  (t/testing "enum with integers"
    (t/is (= {:level 1}
             (c/validate {:level [:enum 1 2 3]}
                         {:level 1}))))

  (t/testing "enum in optional"
    (t/is (= {:name "Alice"}
             (c/validate {:name :string :role [:optional [:enum :admin :user]]}
                         {:name "Alice"}))))

  (t/testing "enum in vector"
    (t/is (= {:statuses ["active" "pending"]}
             (c/validate {:statuses [[:enum "active" "inactive" "pending"]]}
                         {:statuses ["active" "pending"]}))))

  (t/testing "enum with predefined values"
    (let [StatusEnum ["active" "inactive" "pending"]]
      (t/is (= {:status "active"}
               (c/validate {:status [:enum StatusEnum]}
                           {:status "active"})))))

  (t/testing "enum with predefined keyword values"
    (let [RoleEnum [:admin :user :guest]]
      (t/is (= {:role :admin}
               (c/validate {:role [:enum RoleEnum]}
                           {:role :admin}))))))
