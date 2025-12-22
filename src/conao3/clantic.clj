(ns conao3.clantic
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(defn- vector-schema? [v]
  (and (vector? v) (= 1 (count v))))

(declare schema->malli)

(defn- convert-schema [v]
  (cond
    (vector-schema? v) [:vector (convert-schema (first v))]
    (map? v) (schema->malli v)
    :else v))

(defn- schema->malli [schema]
  (->> schema
       (map (fn [[k v]] [k (convert-schema v)]))
       (into [:map])))

(declare select-by-schema)

(defn- select-value [schema-v v]
  (cond
    (vector-schema? schema-v) (mapv #(select-value (first schema-v) %) v)
    (map? schema-v) (select-by-schema schema-v v)
    :else v))

(defn- select-by-schema [schema value]
  (->> value
       (map (fn [[k v]]
              (when-let [schema-v (get schema k)]
                [k (select-value schema-v v)])))
       (filter some?)
       (into {})))

(defn- explain-humanized [schema value]
  (some-> (m/explain schema value) me/humanize))

(defn validate [schema value]
  (let [malli-schema (schema->malli schema)]
    (if (m/validate malli-schema value)
      (select-by-schema schema value)
      (throw (ex-info "Validation failed" {:errors (explain-humanized malli-schema value)
                                           :value value
                                           :schema schema})))))
(m/=> validate [:=> [:cat :map :map] :map])
