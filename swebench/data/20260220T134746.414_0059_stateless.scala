import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat", "sed"), false)
exec("sed", List("-n", "2092,2120p", "./django/db/models/sql/query.py.bak"))