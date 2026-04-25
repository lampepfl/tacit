
// Search for code that handles value assignment in Django models
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models", "def __setattr__", "*.py").take(10).mkString("\n")
)
