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

(defn- default-schema? [v]
  (and (vector? v) (= 3 (count v)) (= :default (first v))))
(m/=> default-schema? [:=> [:cat :any] :boolean])

(declare schema->malli)

(defn- convert-schema [v]
  (cond
    (nil? v) :nil
    (fn? v) (convert-schema (v))
    (default-schema? v) (convert-schema (second v))
    (optional-schema? v) [:maybe (convert-schema (second v))]
    (vector-schema? v) [:vector (convert-schema (first v))]
    (map? v) (schema->malli v)
    :else v))
(m/=> convert-schema [:=> [:cat :any] :any])

(defn- schema->malli [schema]
  (->> schema
       (map (fn [[k v]]
              (cond
                (optional-schema? v) [k {:optional true} (convert-schema v)]
                (default-schema? v) [k {:optional true} (convert-schema v)]
                :else [k (convert-schema v)])))
       (into [:map])))
(m/=> schema->malli [:=> [:cat :map] [:vector :any]])

(declare coerce-by-schema)

(defn- coerce-to-type [target-type v]
  (try
    (case target-type
      :int (cond
             (int? v) v
             (string? v) (Long/parseLong v)
             :else v)
      :double (cond
                (double? v) v
                (int? v) (double v)
                (string? v) (Double/parseDouble v)
                :else v)
      :boolean (cond
                 (boolean? v) v
                 (string? v) (case v
                               "true" true
                               "false" false
                               v)
                 :else v)
      :string (cond
                (string? v) v
                :else (str v))
      :keyword (cond
                 (keyword? v) v
                 (string? v) (keyword v)
                 :else v)
      :symbol (cond
                (symbol? v) v
                (string? v) (symbol v)
                :else v)
      :uuid (cond
              (uuid? v) v
              (string? v) (parse-uuid v)
              :else v)
      v)
    (catch Exception _ v)))
(m/=> coerce-to-type [:=> [:cat :keyword :any] :any])

(defn- coerce-value [schema-v v]
  (cond
    (fn? schema-v) (coerce-value (schema-v) v)
    (keyword? schema-v) (coerce-to-type schema-v v)
    (default-schema? schema-v) (coerce-value (second schema-v) v)
    (optional-schema? schema-v) (when (some? v) (coerce-value (second schema-v) v))
    (vector-schema? schema-v) (mapv #(coerce-value (first schema-v) %) v)
    (map? schema-v) (coerce-by-schema schema-v v)
    :else v))
(m/=> coerce-value [:=> [:cat :any :any] :any])

(defn- coerce-by-schema [schema value]
  (->> schema
       (map (fn [[k schema-v]]
              (when (contains? value k)
                [k (coerce-value schema-v (get value k))])))
       (filter some?)
       (into {})))
(m/=> coerce-by-schema [:=> [:cat :map :map] :map])

(declare select-by-schema)

(defn- select-value [schema-v v]
  (cond
    (fn? schema-v) (select-value (schema-v) v)
    (default-schema? schema-v) (select-value (second schema-v) v)
    (optional-schema? schema-v) (select-value (second schema-v) v)
    (vector-schema? schema-v) (mapv #(select-value (first schema-v) %) v)
    (map? schema-v) (select-by-schema schema-v v)
    :else v))
(m/=> select-value [:=> [:cat :any :any] :any])

(defn- select-by-schema [schema value]
  (->> schema
       (map (fn [[k schema-v]]
              (cond
                (contains? value k) [k (select-value schema-v (get value k))]
                (default-schema? schema-v) [k (nth schema-v 2)]
                (optional-schema? schema-v) nil
                :else [k (get value k)])))
       (filter some?)
       (into {})))
(m/=> select-by-schema [:=> [:cat :map :map] :map])

(defn- explain-humanized [schema value]
  (some-> (m/explain schema value) me/humanize))
(m/=> explain-humanized [:=> [:cat :any :any] [:maybe :map]])

(defn validate [schema value]
  (let [coerced-value (coerce-by-schema schema value)
        malli-schema (schema->malli schema)]
    (if (m/validate malli-schema coerced-value)
      (select-by-schema schema coerced-value)
      (throw (ex-info "Validation failed" {:errors (explain-humanized malli-schema coerced-value)
                                           :value value
                                           :schema schema})))))
(m/=> validate [:=> [:cat :map :map] :map])
