
// Let's look for to_python or value_from_object methods in Field class
// These are typically where value conversion happens
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/fields/__init__.py", "def to_python").take(10).mkString("\n")
)
