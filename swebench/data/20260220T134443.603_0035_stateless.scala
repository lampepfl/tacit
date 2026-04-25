import library.*

given ProcessPermission = new ProcessPermission(Set("sed", "cat"), false)
exec("sed", List("-n", "2071,2090p", "./django/db/models/sql/query.py"))