
// Read value_from_object method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/fields/__init__.py").readLines().drop(917).take(10).mkString("\n")
)
