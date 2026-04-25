
// Read the Field class
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/fields/__init__.py").readLines().drop(85).take(150).mkString("\n")
)
