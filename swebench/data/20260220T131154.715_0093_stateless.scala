
// Search for value_from_object in Field
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/fields/__init__.py", "def value_from_object").take(5).mkString("\n")
)
