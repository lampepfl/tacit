import library.*

given ProcessPermission = new ProcessPermission(Set("ls", "find", "grep"), false)
exec("ls", List("."))