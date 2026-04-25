import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

val testScript = """
# The logic should be:
# When in only mode (defer=False) and adding deferred fields:
# - Remove fields from the immediate loading set
# - If the result is empty -> switch to defer mode (True) to defer everything
# - If the result is not empty -> stay in immediate mode (False) but with fewer fields

# Actually wait - that's still wrong for test 2
# Test 2: only('name').defer('name').defer('country')
# After first defer: ({}, True) - defer everything
# After second defer: should still defer everything!

# Hmm, this is tricky. The issue is that once we enter "defer everything" mode,
# subsequent defers should still keep us in "defer everything" mode.

# Let me think about this differently. The key insight is:
# - defer=False means "only load these fields" (the set contains fields to load)
# - defer=True means "don't load these fields" (the set contains fields to NOT load)

# When we do only("name"), we set: ({'name'}, False) = only load 'name'
# When we then do defer("name"), we want: don't load 'name' = load nothing = defer everything
# Which is: ({}, True)

# When we do only("name", "country"), we set: ({'name', 'country'}, False)
# When we then do defer("name"), we want: don't load 'name', but still load 'country'
# Which is: load 'country' = ({'country'}, False) - NOT ({'country'}, True)

# So the fix needs to:
# - If after removing deferred fields from immediate loading set, the set becomes empty
#   -> switch to defer mode (True) because we're deferring everything
# - If the set is not empty
#   -> stay in immediate mode (False) with the remaining fields

# But wait, test 2: only('name').defer('name').defer('country')
# After first defer: ({}, True) - defer everything  
# After second defer: we're in defer mode (True) already, so we add to deferred set
#   existing = {}, defer = True
#   existing.union({'country'}) = {'country'}
#   Result: ({'country'}, True)

# But expected is: defer everything = ({}, True)
# So we need a special case: if we're in defer mode with empty set, subsequent defers
# should keep the empty set (or at least not add to it)

# Actually wait - let me re-read the problem statement:
# "Company.objects.only('name').defer('name').defer('country')"
# "also loads all the fields with the same query"
# "In those two cases, i would expect the sql query to be: SELECT company.id FROM company"

# So the expected behavior is to defer everything (only load id).
# With my current fix: ({'country'}, True) - this defers country, which means
# id and country are NOT loaded, but name AND trade_number ARE loaded... 
# That's wrong!

# Actually wait - let me re-check the meaning:
# - defer=True, deferred_fields={'country'} means: 
#   "defer 'country', load everything else"
# - defer=True, deferred_fields={} means:
#   "defer nothing = load everything" - NO wait, that's wrong too!

# Let me re-read the code to understand the semantics...
"""

print(testScript)
