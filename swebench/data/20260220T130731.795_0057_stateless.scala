
// Search for code that handles choices or value conversion in Django's model fields
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models", "choices", "*.py").take(20).mkString("\n")
)
