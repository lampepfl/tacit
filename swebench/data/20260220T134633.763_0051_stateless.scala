import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)

val testScript = """
# Let's understand the expected behavior better
# deferred_loading = (fields, defer_flag)
# - defer_flag=True: fields are EXPLICITLY deferred, everything else loads immediately
# - defer_flag=False: fields are EXPLICITLY loaded immediately, everything else is deferred

# Default: (frozenset(), True) - nothing explicitly deferred, so everything loads immediately

# only("name") -> ({'name'}, False) - only 'name' loads immediately
# defer("name") after only -> we want to DEFER 'name', so:
#   - remove 'name' from the immediate loading set: {} - {'name'} = {}
#   - but now we need to switch to defer mode because we're deferring
#   - so: ({}, True) - nothing explicitly deferred, meaning EVERYTHING is deferred!

# Let me also check what the original (unfixed) behavior was:
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

print("ORIGINAL (buggy) behavior:")
print("only('name').defer('name'):")
q = MockQueryOriginal()
q.add_immediate_loading(["name"])
print(f"  After only: {q.deferred_loading}")
q.add_deferred_loading(["name"])
print(f"  After defer: {q.deferred_loading}")
print("  -> defer flag is False, meaning it's trying to immediately load empty set!")

print("\\nonly('name').defer('name').defer('country'):")
q2 = MockQueryOriginal()
q2.add_immediate_loading(["name"])
q2.add_deferred_loading(["name"])
q2.add_deferred_loading(["country"])
print(f"  After defers: {q2.deferred_loading}")
"""

exec("python3", List("-c", testScript))