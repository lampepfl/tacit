import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat"), false)
exec("sed", List("-n", "2084,2095p", "./django/db/models/sql/query.py"))