(ns clantic.core
  (:require
   [malli.core :as m]))

(m/=> hello [:=> [:cat :string] :string])
(defn hello [name]
  (str "Hello, " name "!"))
