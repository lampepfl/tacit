
// Let's look at the CharField and IntegerField classes
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models/fields", "class CharField", "*.py").take(5).mkString("\n")
)
