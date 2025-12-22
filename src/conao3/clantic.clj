(ns conao3.clantic
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(defn- schema->malli [schema]
  (->> schema
       (map (fn [[k v]]
              (if (map? v)
                [k (schema->malli v)]
                [k v])))
       (into [:map])))

(defn- select-by-schema [schema value]
  (->> value
       (map (fn [[k v]]
              (when-let [schema-v (get schema k)]
                [k (if (map? schema-v)
                     (select-by-schema schema-v v)
                     v)])))
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
