
// Let's read the first few lines to see where to add the import
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/fields/__init__.py").readLines().take(10).mkString("\n")
)
