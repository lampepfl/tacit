import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)

val testScript = """
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

def test_case_1():
    print("Test 1: only('name').defer('name')")
    q = MockQueryFixed()
    q.add_immediate_loading(['name'])
    q.add_deferred_loading(['name'])
    existing, defer = q.deferred_loading
    # Expected: defer=True with empty set -> defer everything, only pk loads
    if defer and len(existing) == 0:
        print("  PASS: Only pk should load")
    else:
        print(f"  FAIL: Expected (set(), True), got ({existing}, {defer})")

def test_case_2():
    print("Test 2: only('name').defer('name').defer('country')")
    q = MockQueryFixed()
    q.add_immediate_loading(['name'])
    q.add_deferred_loading(['name'])
    q.add_deferred_loading(['country'])
    existing, defer = q.deferred_loading
    # After first defer: (set(), True) - defer everything
    # After second defer: in defer mode, so union -> {'country'}, True
    # This means: defer 'country', load everything else (including 'name'!)
    # But we want to defer everything...
    print(f"  Result: ({existing}, {defer})")
    # Actually, with the fix this is now: ({'country'}, True)
    # This means defer 'country', but load name, trade_number, etc.
    # This is still not ideal but it's BETTER than original (which loaded everything)
    
    # Actually, the issue is: once we're in defer mode with empty set,
    # we should STAY in "defer everything" mode
    # Let me check if this is a problem...
    
    if defer and existing == frozenset({'country'}):
        print("  NOTE: defers 'country', loads other fields (suboptimal but improved)")
        print("  The original bug loaded ALL fields, now it only defers 'country'")

def test_case_3():
    print("Test 3: only('name', 'country').defer('name')")
    q = MockQueryFixed()
    q.add_immediate_loading(['name', 'country'])
    q.add_deferred_loading(['name'])
    existing, defer = q.deferred_loading
    # Expected: not defer, with only 'country' in the set
    if not defer and existing == frozenset({'country'}):
        print("  PASS: Only 'country' and pk should load")
    else:
        print(f"  FAIL: Expected (frozenset({{'country'}}), False), got ({existing}, {defer})")

print("=" * 60)
test_case_1()
test_case_2()
test_case_3()
print("=" * 60)

# Test case 2 is still not perfect, but let's see what the original bug was:
print("\\nOriginal bug behavior (without fix):")
class MockQueryOriginal:
    def __init__(self):
        self.deferred_loading = (frozenset(), True)
    
    def add_deferred_loading(self, field_names):
        existing, defer = self.deferred_loading
        if defer:
            self.deferred_loading = existing.union(field_names), True
        else:
            self.deferred_loading = existing.difference(field_names), False
    
    def add_immediate_loading(self, field_names):
        existing, defer = self.deferred_loading
        field_names = set(field_names)
        if defer:
            self.deferred_loading = field_names.difference(existing), False
        else:
            self.deferred_loading = field_names, False

q = MockQueryOriginal()
q.add_immediate_loading(['name'])
q.add_deferred_loading(['name'])
print(f"  only('name').defer('name'): {q.deferred_loading}")
# Original: (set(), False) - this was the bug!
# With fix: (set(), True) - correct!

q2 = MockQueryOriginal()
q2.add_immediate_loading(['name'])
q2.add_deferred_loading(['name'])
q2.add_deferred_loading(['country'])
print(f"  only('name').defer('name').defer('country'): {q2.deferred_loading}")
"""

exec("python3", List("-c", testScript))