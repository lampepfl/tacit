import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat"), false)
exec("grep", List("-n", "difference(field_names),", "./django/db/models/sql/query.py"))