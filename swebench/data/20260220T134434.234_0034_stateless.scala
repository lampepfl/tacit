import library.*

given ProcessPermission = new ProcessPermission(Set("sed", "cat"), false)
exec("sed", List("-n", "2065,2120p", "./django/db/models/sql/query.py"))