# clantic

A pydantic-inspired validation library for Clojure using malli.

## Installation

Add to your `deps.edn`:

```clojure
{:deps {io.github.conao/clojure-clantic {:git/tag "v0.1.0" :git/sha "xxxxxxx"}}}
```

## Usage

```clojure
(require '[clantic.core :as c])

(c/model-validate {:name :string :age :int}
                  {:name "Alice" :age 30})
;; => {:name "Alice" :age 30}

(c/model-validate {:name :string :age :int}
                  {:name "Alice" :age "30"})
;; => throws ExceptionInfo
```

## API

### model-validate

Validates a value against a schema and returns the value if valid.

```clojure
(c/model-validate schema value)
```

**Arguments:**
- `schema` - A map of keyword to malli schema type (e.g., `{:name :string :age :int}`)
- `value` - The map to validate

**Returns:**
- The original value if valid

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
