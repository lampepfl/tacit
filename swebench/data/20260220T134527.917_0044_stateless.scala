import library.*

given ProcessPermission = new ProcessPermission(Set("ls", "cat"), false)
exec("ls", List("."))