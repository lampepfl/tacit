
// Search for __set__ in the fields/__init__.py
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/fields/__init__.py", "def __set__").take(5).mkString("\n")
)
