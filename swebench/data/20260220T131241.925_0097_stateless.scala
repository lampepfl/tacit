
// First, let's check if enum is already imported in fields/__init__.py
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/fields/__init__.py").readLines().take(30).mkString("\n")
)
