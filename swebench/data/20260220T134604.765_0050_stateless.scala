import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)

val testScript = """
import sys
sys.path.insert(0, '.')

# Test the deferred_loading logic directly
class MockQuery:
    def __init__(self):
        self.deferred_loading = (frozenset(), True)  # default: no deferred fields
    
    def clear_deferred_loading(self):
        self.deferred_loading = (frozenset(), True)
    
    def add_deferred_loading(self, field_names):
        existing, defer = self.deferred_loading
        if defer:
            self.deferred_loading = existing.union(field_names), True
        else:
            # This is the FIXED line:
            self.deferred_loading = existing.difference(field_names), True  # Changed False to True
    
    def add_immediate_loading(self, field_names):
        existing, defer = self.deferred_loading
        field_names = set(field_names)
        if defer:
            self.deferred_loading = field_names.difference(existing), False
        else:
            self.deferred_loading = field_names, False

# Test 1: only("name").defer("name")
print("Test 1: only('name').defer('name')")
q = MockQuery()
q.add_immediate_loading(["name"])
print(f"After only('name'): {q.deferred_loading}")
q.add_deferred_loading(["name"])
print(f"After defer('name'): {q.deferred_loading}")
existing, defer = q.deferred_loading
if defer and len(existing) == 0:
    print("PASS: All fields deferred (only pk should load)")
else:
    print(f"FAIL: expected (frozenset(), True), got ({existing}, {defer})")

# Test 2: only("name").defer("name").defer("country")
print("\\nTest 2: only('name').defer('name').defer('country')")
q2 = MockQuery()
q2.add_immediate_loading(["name"])
q2.add_deferred_loading(["name"])
q2.add_deferred_loading(["country"])
print(f"After defer('country'): {q2.deferred_loading}")
existing, defer = q2.deferred_loading
if defer and len(existing) == 0:
    print("PASS: All fields deferred (only pk should load)")
else:
    print(f"FAIL: expected (frozenset(), True), got ({existing}, {defer})")

# Test 3: only("name", "country").defer("name") - should work correctly
print("\\nTest 3: only('name', 'country').defer('name')")
q3 = MockQuery()
q3.add_immediate_loading(["name", "country"])
print(f"After only('name', 'country'): {q3.deferred_loading}")
q3.add_deferred_loading(["name"])
print(f"After defer('name'): {q3.deferred_loading}")
existing, defer = q3.deferred_loading
if not defer and existing == frozenset({'country'}):
    print("PASS: Only country is immediate load")
else:
    print(f"FAIL: expected (frozenset({{'country'}}), False), got ({existing}, {defer})")
"""

exec("python3", List("-c", testScript))