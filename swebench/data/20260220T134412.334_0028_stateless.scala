import library.*

given ProcessPermission = new ProcessPermission(Set("find", "grep", "cat"), false)
exec("find", List(".", "-name", "*.py", "-path", "*/query.py"))