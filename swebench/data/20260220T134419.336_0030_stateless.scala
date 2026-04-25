import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat", "head"), false)
exec("grep", List("-r", "class QuerySet", ".", "--include=*.py"))