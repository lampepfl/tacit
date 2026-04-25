import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

// Restore from backup first
exec("cp", List("./django/db/models/sql/query.py.bak", "./django/db/models/sql/query.py"))

// Now use Python to make the fix
val pythonCode = """
import sys

with open('./django/db/models/sql/query.py', 'r') as f:
    lines = f.readlines()

# Line 2088 (index 2087) needs to be replaced
# Original: self.deferred_loading = existing.difference(field_names), False
# New: 
#     new_fields = existing.difference(field_names)
#     self.deferred_loading = (new_fields, not new_fields)

new_lines = lines[:2087] + [
    "            new_fields = existing.difference(field_names)\\n",
    "            self.deferred_loading = (new_fields, not new_fields)\\n",
] + lines[2088:]

with open('./django/db/models/sql/query.py', 'w') as f:
    f.writelines(new_lines)

print("Done")
"""

exec("python3", List("-c", pythonCode))