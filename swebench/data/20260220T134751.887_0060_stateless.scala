import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat", "sed"), false)
exec("sed", List("-n", "669,700p", "./django/db/models/sql/query.py.bak"))