import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)

val testScript = """
# Test with original (buggy) code
class MockQueryOriginal:
    def __init__(self):
        self.deferred_loading = (frozenset(), True)
    
    def add_deferred_loading(self, field_names):
        existing, defer = self.deferred_loading
        if defer:
            self.deferred_loading = existing.union(field_names), True
        else:
            # ORIGINAL BUG: stays False
            self.deferred_loading = existing.difference(field_names), False
    
    def add_immediate_loading(self, field_names):
        existing, defer = self.deferred_loading
        field_names = set(field_names)
        if defer:
            self.deferred_loading = field_names.difference(existing), False
        else:
            self.deferred_loading = field_names, False

# Test with fixed code
class MockQueryFixed:
    def __init__(self):
        self.deferred_loading = (frozenset(), True)
    
    def add_deferred_loading(self, field_names):
        existing, defer = self.deferred_loading
        if defer:
            self.deferred_loading = existing.union(field_names), True
        else:
            # FIXED: change to True
            self.deferred_loading = existing.difference(field_names), True
    
    def add_immediate_loading(self, field_names):
        existing, defer = self.deferred_loading
        field_names = set(field_names)
        if defer:
            self.deferred_loading = field_names.difference(existing), False
        else:
            self.deferred_loading = field_names, False

print("=" * 50)
print("Test case from bug report:")
print("=" * 50)

print("\\n1. Company.objects.only('name').defer('name')")
print("Expected: only 'id' should be loaded (all other fields deferred)")

print("\\nOriginal (buggy):")
q1 = MockQueryOriginal()
q1.add_immediate_loading(["name"])
print(f"  After only('name'): {q1.deferred_loading}")
q1.add_deferred_loading(["name"])
print(f"  After defer('name'): {q1.deferred_loading}")
# The bug: ({}, False) means "immediately load nothing", but the code probably 
# treats False + empty set as "load everything" 

print("\\nFixed:")
q1f = MockQueryFixed()
q1f.add_immediate_loading(["name"])
print(f"  After only('name'): {q1f.deferred_loading}")
q1f.add_deferred_loading(["name"])
print(f"  After defer('name'): {q1f.deferred_loading}")
# Fixed: ({}, True) means "defer everything" - correct!

print("\\n2. Company.objects.only('name').defer('name').defer('country')")
print("Expected: only 'id' should be loaded")

print("\\nOriginal (buggy):")
q2 = MockQueryOriginal()
q2.add_immediate_loading(["name"])
q2.add_deferred_loading(["name"])
q2.add_deferred_loading(["country"])
print(f"  Result: {q2.deferred_loading}")

print("\\nFixed:")
q2f = MockQueryFixed()
q2f.add_immediate_loading(["name"])
q2f.add_deferred_loading(["name"])
q2f.add_deferred_loading(["country"])
print(f"  Result: {q2f.deferred_loading}")

print("\\n3. Company.objects.only('name', 'country').defer('name') - should already work")
print("Expected: 'id' and 'country' loaded")

print("\\nOriginal:")
q3 = MockQueryOriginal()
q3.add_immediate_loading(["name", "country"])
print(f"  After only: {q3.deferred_loading}")
q3.add_deferred_loading(["name"])
print(f"  After defer: {q3.deferred_loading}")

print("\\nFixed:")
q3f = MockQueryFixed()
q3f.add_immediate_loading(["name", "country"])
print(f"  After only: {q3f.deferred_loading}")
q3f.add_deferred_loading(["name"])
print(f"  After defer: {q3f.deferred_loading}")
"""

exec("python3", List("-c", testScript))