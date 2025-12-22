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
;;=> throws ExceptionInfo

(c/validate {:a :int} {:a 42 :b 2})
;;=> {:a 42}  (extra keys are removed)

(c/validate {:user {:name :string :age :int}}
            {:user {:name "Alice" :age 30}})
;;=> {:user {:name "Alice" :age 30}}

(c/validate {:user {:name :string}}
            {:user {:name "Alice" :age 30 :extra "data"}})
;;=> {:user {:name "Alice"}}  (nested extra keys are also removed)
```

## API

### validate

Validates a value against a schema and returns only the keys defined in the schema.

```clojure
(c/validate schema value)
```

**Arguments:**
- `schema` - A map of keyword to malli schema type or nested map (e.g., `{:name :string :age :int}` or `{:user {:name :string}}`)
- `value` - The map to validate

**Returns:**
- A map containing only the keys defined in the schema (extra keys are removed)

**Throws:**
- `ExceptionInfo` if validation fails, with ex-data containing:
  - `:errors` - humanized error messages as a map
  - `:value` - the original value
  - `:schema` - the original schema

**Supported types:**
- `:string` - string values
- `:int` - integer values
- `:double` - floating point values
- `:boolean` - boolean values
- `:keyword` - keyword values
- `:symbol` - symbol values
- `:uuid` - UUID values
- `:nil` - nil value

## Development

```bash
make repl  # Start REPL
make test  # Run tests
make lint  # Run clj-kondo
```

## License

See LICENSE file.
