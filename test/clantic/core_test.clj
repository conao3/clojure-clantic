(ns clantic.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [clantic.core :as sut]))

(deftest hello-test
  (is (= "Hello, world!" (sut/hello "world"))))
