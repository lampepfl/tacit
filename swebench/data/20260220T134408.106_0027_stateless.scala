import library.*

given ProcessPermission = new ProcessPermission(Set("find", "grep", "cat"), false)
given FileSystem = new FileSystem:
  def access(path: String): FileEntry^{this} = throw new Exception("not implemented - use shell")

grepRecursive(".", "def defer", "*.py")