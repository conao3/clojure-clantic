(ns clantic.core
  (:require
   [malli.core :as m]))

(defn hello [name]
  (str "Hello, " name "!"))
(m/=> hello [:=> [:cat :int] :string])
