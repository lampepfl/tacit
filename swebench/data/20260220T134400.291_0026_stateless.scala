import library.*

given ProcessPermission = new ProcessPermission(Set("find", "grep"), false)
grepRecursive(".", "def defer", "*.py")