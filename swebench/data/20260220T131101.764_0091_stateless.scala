
// Search for __set__ in the fields directory
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models/fields", "def __set__", "*.py").take(10).mkString("\n")
)
