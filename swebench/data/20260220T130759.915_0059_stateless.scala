
// Search for Model class definition
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models", "class Model", "*.py").take(10).mkString("\n")
)
