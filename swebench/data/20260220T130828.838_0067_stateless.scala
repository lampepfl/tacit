
// Search for DeferredAttribute
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models", "class DeferredAttribute", "*.py").take(5).mkString("\n")
)
