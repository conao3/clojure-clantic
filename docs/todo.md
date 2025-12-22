# TODO: Pydantic Feature Parity

## High Priority

- [ ] Optional type - distinguish required vs optional fields
- [ ] Default values - provide defaults when fields are missing
- [ ] List/Vector type - validate collections like `[{:name :string}]`
- [ ] Type coercion - auto-convert compatible types (e.g., `"30"` to `30`)

## Type System

- [ ] Union type - `[:or :string :int]`
- [ ] Literal type - `[:enum "a" "b" "c"]`
- [ ] Enum support
- [ ] DateTime types - `:date`, `:time`, `:instant`
- [ ] Constrained string types - email, URL, regex pattern
- [ ] Constrained numeric types - min/max, positive, negative
- [ ] Set type
- [ ] Map with typed keys/values - `[:map-of :keyword :string]`

## Schema Features

- [ ] Field aliases
- [ ] Custom validators (field-level)
- [ ] Model validators (cross-field validation)
- [ ] Computed fields
- [ ] Nested model references

## Model Features

- [ ] Model definition macro (BaseModel equivalent)
- [ ] Model inheritance
- [ ] JSON Schema generation
- [ ] Serialization customization
- [ ] Strict mode vs lax mode toggle

## Other

- [ ] Configuration options (extra keys handling, etc.)
- [ ] Generic models
- [ ] Discriminated unions
- [ ] Recursive model support
