import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

val pythonCode = """
with open('./django/db/models/sql/query.py', 'r') as f:
    lines = f.readlines()

# Find the exact line to replace
for i, line in enumerate(lines):
    if 'self.deferred_loading = existing.difference(field_names), False' in line:
        print(f'Found at line {i}: {repr(line)}')
        # Replace this line with two new lines
        lines[i] = '            new_fields = existing.difference(field_names)\\n'
        lines.insert(i+1, '            self.deferred_loading = (new_fields, not new_fields)\\n')
        break

with open('./django/db/models/sql/query.py', 'w') as f:
    f.writelines(lines)

print('Done')
"""

exec("python3", List("-c", pythonCode))