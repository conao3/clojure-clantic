# TODO: Pydantic Feature Parity

## High Priority

- [x] Optional type - distinguish required vs optional fields
- [x] Default values - provide defaults when fields are missing
- [x] List/Vector type - validate collections like `[{:name :string}]`
- [x] Type coercion - auto-convert compatible types (e.g., `"30"` to `30`)

## Type System

- [x] Union type - `[:or :string :int]`
- [x] Literal type - `[:enum "a" "b" "c"]`
- [x] Enum support - `[:enum EnumVector]`
- [x] DateTime types - `:local-date`, `:local-time`, `:local-date-time`, `:offset-date-time`
- [ ] Constrained string types - email, URL, regex pattern
- [ ] Constrained numeric types - min/max, positive, negative
- [x] Set type - `[:set :int]`
- [x] Map with typed keys/values - `[:map-of :keyword :string]`

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
