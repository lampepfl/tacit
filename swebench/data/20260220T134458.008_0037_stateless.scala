import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat", "python3"), false)
exec("grep", List("-n", "difference(field_names), False", "./django/db/models/sql/query.py"))