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
