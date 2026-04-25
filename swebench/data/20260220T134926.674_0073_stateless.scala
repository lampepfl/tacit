import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

// Restore from backup
exec("cp", List("./django/db/models/sql/query.py.bak", "./django/db/models/sql/query.py"))

val pythonCode = """
with open('./django/db/models/sql/query.py', 'r') as f:
    content = f.read()

# Replace the specific line
old_line = '            self.deferred_loading = existing.difference(field_names), False'
new_content = '''            new_fields = existing.difference(field_names)
            self.deferred_loading = (new_fields, not new_fields)'''

content = content.replace(old_line, new_content)

with open('./django/db/models/sql/query.py', 'w') as f:
    f.write(content)

print('Done')
"""

exec("python3", List("-c", pythonCode))