#!/usr/bin/env -S uv run
# /// script
# requires-python = ">=3.12"
# dependencies = [
#     "pydantic>=2.0",
# ]
# ///

from pydantic import BaseModel, ValidationError


class User(BaseModel):
    name: str
    age: int


class Nested(BaseModel):
    user: User


print("=== Basic validation ===")
user = User(name="Alice", age=30)
print(f"Valid: {user}")
print(f"model_dump: {user.model_dump()}")

print("\n=== Extra keys are ignored by default ===")
user2 = User.model_validate({"name": "Bob", "age": 25, "extra": "ignored"})
print(f"Extra keys ignored: {user2}")
print(f"model_dump: {user2.model_dump()}")

print("\n=== Validation error ===")
try:
    User(name="Charlie", age="not an int")
except ValidationError as e:
    print(f"Error: {e}")

print("\n=== Nested validation ===")
nested = Nested(user={"name": "Dave", "age": 40})
print(f"Nested: {nested}")
print(f"model_dump: {nested.model_dump()}")

print("\n=== Nested with extra keys ===")
nested2 = Nested.model_validate({"user": {"name": "Eve", "age": 35, "extra": "data"}})
print(f"Nested extra keys: {nested2}")
print(f"model_dump: {nested2.model_dump()}")

print("\n=== Nested validation error ===")
try:
    Nested(user={"name": "Frank", "age": "invalid"})
except ValidationError as e:
    print(f"Error: {e}")

print("\n=== Missing required field ===")
try:
    User(name="Grace")
except ValidationError as e:
    print(f"Error: {e}")

print("\n=== Coercion examples ===")

print("\n--- String to int ---")
user3 = User(name="Henry", age="42")
print(f"age='42' -> {user3.age} (type: {type(user3.age).__name__})")

print("\n--- Int to string (NOT allowed in pydantic v2) ---")
try:
    user4 = User(name=123, age=30)
    print(f"name=123 -> {user4.name} (type: {type(user4.name).__name__})")
except ValidationError as e:
    print(f"name=123 -> Error (int to string not coerced)")

print("\n--- Boolean coercion ---")
from pydantic import BaseModel as BM
class BoolModel(BM):
    flag: bool

print(f"flag=True -> {BoolModel(flag=True).flag}")
print(f"flag='true' -> {BoolModel(flag='true').flag}")
print(f"flag='True' -> {BoolModel(flag='True').flag}")
print(f"flag='yes' -> {BoolModel(flag='yes').flag}")
print(f"flag='1' -> {BoolModel(flag='1').flag}")
print(f"flag=1 -> {BoolModel(flag=1).flag}")
print(f"flag='false' -> {BoolModel(flag='false').flag}")
print(f"flag='no' -> {BoolModel(flag='no').flag}")
print(f"flag='0' -> {BoolModel(flag='0').flag}")
print(f"flag=0 -> {BoolModel(flag=0).flag}")

print("\n--- None/null coercion ---")
from typing import Optional
class OptionalModel(BM):
    value: Optional[int] = None

print(f"value=None -> {OptionalModel(value=None).value}")
print(f"value missing -> {OptionalModel().value}")
try:
    result = OptionalModel(value="null")
    print(f"value='null' -> {result.value}")
except ValidationError as e:
    print(f"value='null' -> Error: {e}")

try:
    result = OptionalModel(value="None")
    print(f"value='None' -> {result.value}")
except ValidationError as e:
    print(f"value='None' -> Error: {e}")

print("\n--- UUID coercion ---")
from uuid import UUID
class UUIDModel(BM):
    id: UUID

uuid_str = "550e8400-e29b-41d4-a716-446655440000"
print(f"id='{uuid_str}' -> {UUIDModel(id=uuid_str).id}")

print("\n--- Float/Double coercion ---")
class FloatModel(BM):
    price: float

print(f"price='3.14' -> {FloatModel(price='3.14').price}")
print(f"price=3 -> {FloatModel(price=3).price}")
