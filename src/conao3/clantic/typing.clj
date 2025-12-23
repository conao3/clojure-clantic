(ns conao3.clantic.typing
  (:refer-clojure :exclude [str int double keyword symbol seq])
  (:require
   [malli.core :as m]))

(defn str [] :string)
(m/=> str [:=> [:cat] :keyword])

(defn int [] :int)
(m/=> int [:=> [:cat] :keyword])

(defn double [] :double)
(m/=> double [:=> [:cat] :keyword])

(defn bool [] :boolean)
(m/=> bool [:=> [:cat] :keyword])

(defn keyword [] :keyword)
(m/=> keyword [:=> [:cat] :keyword])

(defn symbol [] :symbol)
(m/=> symbol [:=> [:cat] :keyword])

(defn uuid [] :uuid)
(m/=> uuid [:=> [:cat] :keyword])

(defn local-date [] :local-date)
(m/=> local-date [:=> [:cat] :keyword])

(defn local-time [] :local-time)
(m/=> local-time [:=> [:cat] :keyword])

(defn local-date-time [] :local-date-time)
(m/=> local-date-time [:=> [:cat] :keyword])

(defn offset-date-time [] :offset-date-time)
(m/=> offset-date-time [:=> [:cat] :keyword])

(defn seq [schema-fn]
  [schema-fn])
(m/=> seq [:=> [:cat :any] [:vector :any]])

(defn optional [schema-fn]
  [:optional schema-fn])
(m/=> optional [:=> [:cat :any] [:vector :any]])

(defn default [schema-fn default-value]
  [:default schema-fn default-value])
(m/=> default [:=> [:cat :any :any] [:vector :any]])

(defn union [& schema-fns]
  (into [:or] schema-fns))
(m/=> union [:=> [:cat [:* :any]] [:vector :any]])

(defn enum [& values]
  (into [:enum] values))
(m/=> enum [:=> [:cat [:* :any]] [:vector :any]])
