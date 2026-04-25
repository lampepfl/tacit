import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

val testScript = """
# Looking at the code, the semantics are:
# - defer=True, field_names=X: defer fields in X, load everything else
# - defer=False, field_names=X: only load fields in X
# 
# But there's also: if not field_names: return (load everything by default)
#
# So:
# - ({}, True): empty field_names -> return early -> load everything
#   BUT this is interpreted as "defer nothing" which should load everything - OK
# - ({}, False): empty field_names -> return early -> load everything
#   BUT this is "only load nothing" which should load nothing - BUG!
#
# The fix: when only has one field and we defer it, we should get ({}, True)
# because ({}, False) incorrectly loads everything.

# Let me verify my simple fix works for the main bug case:
class MockQuerySimpleFix:
    def __init__(self):
        self.deferred_loading = (frozenset(), True)
    
    def add_deferred_loading(self, field_names):
        existing, defer = self.deferred_loading
        if defer:
            self.deferred_loading = existing.union(field_names), True
        else:
            # Simple fix: change False to True
            self.deferred_loading = existing.difference(field_names), True
    
    def add_immediate_loading(self, field_names):
        existing, defer = self.deferred_loading
        field_names = set(field_names)
        if defer:
            self.deferred_loading = field_names.difference(existing), False
        else:
            self.deferred_loading = field_names, False

print("Test 1: only('name').defer('name')")
q = MockQuerySimpleFix()
q.add_immediate_loading(['name'])
print(f'After only: {q.deferred_loading}')
q.add_deferred_loading(['name'])
print(f'After defer: {q.deferred_loading}')
# Result: ({}, True) - defer everything, only pk loads
# This is CORRECT!

print("\\nTest 3: only('name', 'country').defer('name')")
q = MockQuerySimpleFix()
q.add_immediate_loading(['name', 'country'])
print(f'After only: {q.deferred_loading}')
q.add_deferred_loading(['name'])
print(f'After defer: {q.deferred_loading}')
# Result: ({'country'}, True) - defer 'country', load everything else
# But we want: load 'country' (and pk)
# This is WRONG with the simple fix!

print("\\n---")
print("So the simple fix works for Test 1 but breaks Test 3")
print("We need a smarter fix that:")
print("- Changes to defer=True ONLY when the result becomes empty")
print("- Stays defer=False when there are still fields to load")
"""

exec("python3", List("-c", testScript))