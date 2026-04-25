
// Read the Model.save method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/base.py").readLines().drop(662).take(50).mkString("\n")
)
