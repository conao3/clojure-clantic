# clantic

A pydantic-inspired validation library for Clojure using malli.

## Installation

Add to your `deps.edn`:

```clojure
{:deps {io.github.conao/clojure-clantic {:git/tag "v0.1.0" :git/sha "xxxxxxx"}}}
```

## Usage

```clojure
(require '[conao3.clantic :as c])

(c/validate {:name :string :age :int}
            {:name "Alice" :age 30})
;;=> {:name "Alice" :age 30}

(c/validate {:name :string :age :int}
            {:name "Alice" :age "30"})
;;=> {:name "Alice" :age 30}  ; auto-coercion

(c/validate {:a :int} {:a 42 :b 2})
;;=> {:a 42}  ; extra keys removed
```

## Primitive Types

| Type | Description | Coercion |
|------|-------------|----------|
| `:string` | String | - |
| `:int` | Integer | `"42"` → `42` |
| `:double` | Float | `"3.14"` → `3.14`, `3` → `3.0` |
| `:boolean` | Boolean | `"true"` → `true`, `"false"` → `false` |
| `:keyword` | Keyword | `"foo"` → `:foo` |
| `:symbol` | Symbol | `"foo"` → `foo` |
| `:uuid` | UUID | `"550e8400-..."` → `#uuid "550e8400-..."` |
| `:nil` | Nil | - |

## DateTime Types

| Type | Java Class | Example |
|------|------------|---------|
| `:local-date` | LocalDate | `"2024-01-15"` |
| `:local-time` | LocalTime | `"10:30:00"` |
| `:local-date-time` | LocalDateTime | `"2024-01-15T10:30:00"` |
| `:offset-date-time` | OffsetDateTime | `"2024-01-15T10:30:00+09:00"` |

```clojure
(c/validate {:date :local-date}
            {:date "2024-01-15"})
;;=> {:date #object[java.time.LocalDate "2024-01-15"]}
```

## Composite Types

### Nested Map

```clojure
(c/validate {:user {:name :string :age :int}}
            {:user {:name "Alice" :age 30}})
;;=> {:user {:name "Alice" :age 30}}
```

### Vector

```clojure
(c/validate {:ids [:int]}
            {:ids [1 2 3]})
;;=> {:ids [1 2 3]}

(c/validate {:users [{:name :string}]}
            {:users [{:name "Alice"} {:name "Bob"}]})
;;=> {:users [{:name "Alice"} {:name "Bob"}]}
```

### Set

```clojure
(c/validate {:tags [:set :string]}
            {:tags #{"a" "b" "c"}})
;;=> {:tags #{"a" "b" "c"}}
```

### Map-of

```clojure
(c/validate {:headers [:map-of :keyword :string]}
            {:headers {:content-type "application/json"}})
;;=> {:headers {:content-type "application/json"}}

(c/validate {:scores [:map-of :string :int]}
            {:scores {"alice" "100" "bob" "85"}})
;;=> {:scores {"alice" 100 "bob" 85}}  ; value coercion
```

## Union Type

```clojure
(c/validate {:id [:or :string :int]}
            {:id "abc"})
;;=> {:id "abc"}

(c/validate {:id [:or :string :int]}
            {:id 123})
;;=> {:id 123}

(c/validate {:id [:or :int :string]}
            {:id "42"})
;;=> {:id 42}  ; first matching type wins (with coercion)
```

## Enum Type

```clojure
(c/validate {:status [:enum "active" "inactive" "pending"]}
            {:status "active"})
;;=> {:status "active"}

(c/validate {:role [:enum :admin :user :guest]}
            {:role :admin})
;;=> {:role :admin}

;; with predefined values
(def StatusEnum ["active" "inactive" "pending"])
(c/validate {:status [:enum StatusEnum]}
            {:status "active"})
;;=> {:status "active"}
```

## Modifiers

### Optional

```clojure
(c/validate {:name :string :age [:optional :int]}
            {:name "Alice"})
;;=> {:name "Alice"}

(c/validate {:name :string :age [:optional :int]}
            {:name "Alice" :age nil})
;;=> {:name "Alice" :age nil}
```

### Default

```clojure
(c/validate {:name :string :age [:default :int 0]}
            {:name "Alice"})
;;=> {:name "Alice" :age 0}
```

## Typing DSL

For a more readable schema definition:

```clojure
(require '[conao3.clantic :as c])
(require '[conao3.clantic.typing :as ct])

(c/validate {:name ct/str :age ct/int}
            {:name "Alice" :age 30})

(c/validate {:ids (ct/seq ct/int)}
            {:ids [1 2 3]})

(c/validate {:tags (ct/set-of ct/keyword)}
            {:tags #{:a :b}})

(c/validate {:data (ct/map-of ct/keyword ct/str)}
            {:data {:a "1" :b "2"}})

(c/validate {:id (ct/union ct/str ct/int)}
            {:id 123})

(c/validate {:status (ct/enum "active" "inactive")}
            {:status "active"})

(c/validate {:age (ct/optional ct/int)}
            {})

(c/validate {:age (ct/default ct/int 0)}
            {})
```

### Available Functions

| Function | Returns | Description |
|----------|---------|-------------|
| `ct/str` | `:string` | String type |
| `ct/int` | `:int` | Integer type |
| `ct/double` | `:double` | Float type |
| `ct/bool` | `:boolean` | Boolean type |
| `ct/keyword` | `:keyword` | Keyword type |
| `ct/symbol` | `:symbol` | Symbol type |
| `ct/uuid` | `:uuid` | UUID type |
| `ct/local-date` | `:local-date` | LocalDate type |
| `ct/local-time` | `:local-time` | LocalTime type |
| `ct/local-date-time` | `:local-date-time` | LocalDateTime type |
| `ct/offset-date-time` | `:offset-date-time` | OffsetDateTime type |
| `(ct/seq schema)` | `[schema]` | Vector of values |
| `(ct/set-of schema)` | `[:set schema]` | Set of values |
| `(ct/map-of k v)` | `[:map-of k v]` | Map with typed keys/values |
| `(ct/union & schemas)` | `[:or ...]` | Union type |
| `(ct/enum & values)` | `[:enum ...]` | Enum type |
| `(ct/optional schema)` | `[:optional schema]` | Optional field |
| `(ct/default schema v)` | `[:default schema v]` | Default value |

## Error Handling

```clojure
(try
  (c/validate {:age :int} {:age "not-a-number"})
  (catch Exception e
    (ex-data e)))
;;=> {:errors {:age ["should be an integer"]}
;;    :value {:age "not-a-number"}
;;    :schema {:age :int}}
```

## Development

```bash
make repl  # Start REPL
make test  # Run tests
make lint  # Run clj-kondo
```

## License

See LICENSE file.
