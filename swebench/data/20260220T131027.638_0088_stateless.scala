
// Search for code that converts database values to model field values
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models", "converters", "*.py").take(10).mkString("\n")
)
