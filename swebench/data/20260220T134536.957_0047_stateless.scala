import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)
exec("python3", List("tests/runtests.py", "defer", "-v", "2"))