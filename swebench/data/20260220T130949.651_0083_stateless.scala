
// Search for pre_save method in Field
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/fields/__init__.py", "def pre_save").take(5).mkString("\n")
)
