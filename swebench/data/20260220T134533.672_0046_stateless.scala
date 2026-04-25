import library.*

given ProcessPermission = new ProcessPermission(Set("find", "cat"), false)
exec("find", List("tests", "-name", "runtests.py"))