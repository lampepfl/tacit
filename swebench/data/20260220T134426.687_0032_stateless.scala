import library.*

given ProcessPermission = new ProcessPermission(Set("sed", "cat"), false)
exec("sed", List("-n", "1205,1260p", "./django/db/models/query.py"))