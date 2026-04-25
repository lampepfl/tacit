
// Read the to_python method in the base Field class
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/fields/__init__.py").readLines().drop(575).take(30).mkString("\n")
)
