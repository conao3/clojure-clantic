(ns conao3.clantic
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(defn- schema->malli [schema]
  (into [:map] (map (fn [[k v]] [k v]) schema)))

(defn- explain-humanized [schema value]
  (some-> (m/explain schema value) me/humanize))

(defn validate
  [schema value]
  (let [malli-schema (schema->malli schema)]
    (if (m/validate malli-schema value)
      (select-keys value (keys schema))
      (throw (ex-info "Validation failed" {:errors (explain-humanized malli-schema value)
                                           :value value
                                           :schema schema})))))
(m/=> validate [:=> [:cat :map :map] :map])
