(ns conao3.clantic.typing-test
  (:require
   [clojure.test :as t]
   [conao3.clantic :as c]
   [conao3.clantic.typing :as ct]))

(t/deftest primitive-types-test
  (t/testing "str returns :string"
    (t/is (= :string (ct/str))))

  (t/testing "int returns :int"
    (t/is (= :int (ct/int))))

  (t/testing "double returns :double"
    (t/is (= :double (ct/double))))

  (t/testing "bool returns :boolean"
    (t/is (= :boolean (ct/bool))))

  (t/testing "keyword returns :keyword"
    (t/is (= :keyword (ct/keyword))))

  (t/testing "symbol returns :symbol"
    (t/is (= :symbol (ct/symbol))))

  (t/testing "uuid returns :uuid"
    (t/is (= :uuid (ct/uuid))))

  (t/testing "nil can be used directly"
    (t/is (= {:v nil}
             (c/validate {:v nil} {:v nil})))))

(t/deftest composite-types-test
  (t/testing "seq wraps type in vector"
    (t/is (= [ct/int] (ct/seq ct/int))))

  (t/testing "optional wraps type"
    (t/is (= [:optional ct/int] (ct/optional ct/int))))

  (t/testing "optional with nested map"
    (t/is (= [:optional {:name ct/str}]
             (ct/optional {:name ct/str}))))

  (t/testing "default wraps type with value"
    (t/is (= [:default ct/int 0] (ct/default ct/int 0))))

  (t/testing "union wraps types"
    (t/is (= [:or ct/str ct/int] (ct/union ct/str ct/int)))))

(t/deftest integration-test
  (t/testing "schema with typing functions"
    (t/is (= {:name "Alice" :age 30}
             (c/validate {:name ct/str :age ct/int}
                         {:name "Alice" :age 30}))))

  (t/testing "schema with optional"
    (t/is (= {:name "Alice"}
             (c/validate {:name ct/str :age (ct/optional ct/int)}
                         {:name "Alice"}))))

  (t/testing "schema with seq"
    (t/is (= {:ids [1 2 3]}
             (c/validate {:ids (ct/seq ct/int)}
                         {:ids [1 2 3]}))))

  (t/testing "schema with seq of maps"
    (t/is (= {:users [{:name "Alice"} {:name "Bob"}]}
             (c/validate {:users (ct/seq {:name ct/str})}
                         {:users [{:name "Alice" :age 30} {:name "Bob"}]}))))

  (t/testing "schema with default"
    (t/is (= {:name "Alice" :age 0}
             (c/validate {:name ct/str :age (ct/default ct/int 0)}
                         {:name "Alice"}))))

  (t/testing "schema with union"
    (t/is (= {:id "abc"}
             (c/validate {:id (ct/union ct/str ct/int)}
                         {:id "abc"})))
    (t/is (= {:id 123}
             (c/validate {:id (ct/union ct/str ct/int)}
                         {:id 123})))))
