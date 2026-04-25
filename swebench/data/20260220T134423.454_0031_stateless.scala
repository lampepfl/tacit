import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat", "head"), false)
exec("grep", List("-n", "def defer\\|def only", "./django/db/models/query.py"))