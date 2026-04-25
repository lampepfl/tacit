import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)

val testScript = """
# Let me verify the Python boolean logic
print("not {'country'}:", not {'country'})
print("not set():", not set())
print("bool({'country'}):", bool({'country'}))
print("bool(set()):", bool(set()))
"""

exec("python3", List("-c", testScript))