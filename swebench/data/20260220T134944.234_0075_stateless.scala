import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)

val testScript = """
# Test the fixed code logic

class MockQueryFixed:
    def __init__(self):
        self.deferred_loading = (frozenset(), True)
    
    def add_deferred_loading(self, field_names):
        existing, defer = self.deferred_loading
        if defer:
            self.deferred_loading = existing.union(field_names), True
        else:
            new_fields = existing.difference(field_names)
            self.deferred_loading = (new_fields, not new_fields)
    
    def add_immediate_loading(self, field_names):
        existing, defer = self.deferred_loading
        field_names = set(field_names)
        if defer:
            self.deferred_loading = field_names.difference(existing), False
        else:
            self.deferred_loading = field_names, False

print("=" * 60)
print("Testing the fix:")
print("=" * 60)

# Test 1: only("name").defer("name")
print("\\n1. only('name').defer('name')")
print("   Expected: only 'id' should be loaded (all fields deferred)")
q1 = MockQueryFixed()
q1.add_immediate_loading(['name'])
print(f"   After only: {q1.deferred_loading}")
q1.add_deferred_loading(['name'])
print(f"   After defer: {q1.deferred_loading}")
# Expected: (set(), True) - defer=True means defer the empty set = defer everything
# Correct!

# Test 2: only("name").defer("name").defer("country")
print("\\n2. only('name').defer('name').defer('country')")
print("   Expected: only 'id' should be loaded")
q2 = MockQueryFixed()
q2.add_immediate_loading(['name'])
q2.add_deferred_loading(['name'])
print(f"   After defer('name'): {q2.deferred_loading}")
q2.add_deferred_loading(['country'])
print(f"   After defer('country'): {q2.deferred_loading}")
# After first defer: (set(), True) - defer everything
# After second defer: existing={}, defer=True -> union({'country'}) = {'country'}
# Result: ({'country'}, True) - this means defer 'country', load everything else
# But we expected to defer everything... 

# Wait, let me think about this more carefully
# In defer mode (defer=True), the set contains fields to DEFER
# So ({'country'}, True) means: defer 'country', load id and everything else
# That's NOT what we want!

# Actually wait - the original bug report says:
# "Company.objects.only('name').defer('name').defer('country')"
# "also loads all the fields with the same query"
# "In those two cases, i would expect the sql query to be: SELECT company.id FROM company"

# So they expect BOTH name and country to be deferred, leaving only id.
# With current fix: ({'country'}, True) - only 'country' is deferred, not 'name'!

# Hmm, this is still not right. Let me re-check the issue...

print("\\n3. only('name', 'country').defer('name')")
print("   Expected: 'id' and 'country' loaded (this already works)")
q3 = MockQueryFixed()
q3.add_immediate_loading(['name', 'country'])
print(f"   After only: {q3.deferred_loading}")
q3.add_deferred_loading(['name'])
print(f"   After defer: {q3.deferred_loading}")
# Expected: ({'country'}, False) - only load 'country'
# With fix: new_fields = {'country'}, not {'country'} = True
# Result: ({'country'}, True) - WRONG!

print("\\n--- Analysis ---")
print("The fix is still not quite right for test 3")
print("We need: ({'country'}, False) but we get: ({'country'}, True)")
"""

exec("python3", List("-c", testScript))