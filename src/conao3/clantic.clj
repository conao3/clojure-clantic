(ns conao3.clantic
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(defn- vector-schema? [v]
  (and (vector? v) (= 1 (count v)) (not= :optional (first v))))
(m/=> vector-schema? [:=> [:cat :any] :boolean])

(defn- optional-schema? [v]
  (and (vector? v) (= 2 (count v)) (= :optional (first v))))
(m/=> optional-schema? [:=> [:cat :any] :boolean])

(declare schema->malli)

(defn- convert-schema [v]
  (cond
    (nil? v) :nil
    (fn? v) (convert-schema (v))
    (optional-schema? v) [:maybe (convert-schema (second v))]
    (vector-schema? v) [:vector (convert-schema (first v))]
    (map? v) (schema->malli v)
    :else v))
(m/=> convert-schema [:=> [:cat :any] :any])

(defn- schema->malli [schema]
  (->> schema
       (map (fn [[k v]]
              (if (optional-schema? v)
                [k {:optional true} (convert-schema v)]
                [k (convert-schema v)])))
       (into [:map])))
(m/=> schema->malli [:=> [:cat :map] [:vector :any]])

(declare select-by-schema)

(defn- select-value [schema-v v]
  (cond
    (fn? schema-v) (select-value (schema-v) v)
    (optional-schema? schema-v) (select-value (second schema-v) v)
    (vector-schema? schema-v) (mapv #(select-value (first schema-v) %) v)
    (map? schema-v) (select-by-schema schema-v v)
    :else v))
(m/=> select-value [:=> [:cat :any :any] :any])

(defn- select-by-schema [schema value]
  (->> schema
       (map (fn [[k schema-v]]
              (if (contains? value k)
                [k (select-value schema-v (get value k))]
                (when-not (optional-schema? schema-v)
                  [k (get value k)]))))
       (filter some?)
       (into {})))
(m/=> select-by-schema [:=> [:cat :map :map] :map])

(defn- explain-humanized [schema value]
  (some-> (m/explain schema value) me/humanize))
(m/=> explain-humanized [:=> [:cat :any :any] [:maybe :map]])

(defn validate [schema value]
  (let [malli-schema (schema->malli schema)]
    (if (m/validate malli-schema value)
      (select-by-schema schema value)
      (throw (ex-info "Validation failed" {:errors (explain-humanized malli-schema value)
                                           :value value
                                           :schema schema})))))
(m/=> validate [:=> [:cat :map :map] :map])
