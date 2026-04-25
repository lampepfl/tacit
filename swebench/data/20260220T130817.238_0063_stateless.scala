
// Search for the Field class __set__ method (descriptor)
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models/fields", "def __set__", "*.py").take(10).mkString("\n")
)
