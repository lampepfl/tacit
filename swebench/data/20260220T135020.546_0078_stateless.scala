import library.*

given ProcessPermission = new ProcessPermission(Set("diff", "cat"), false)
exec("diff", List("-u", "./django/db/models/sql/query.py.bak", "./django/db/models/sql/query.py"))