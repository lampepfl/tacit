
// Look at the Field class in __init__.py
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/fields/__init__.py", "class Field").take(10).mkString("\n")
)
