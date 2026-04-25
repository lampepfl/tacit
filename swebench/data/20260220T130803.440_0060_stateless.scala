
// Read the Model class definition in base.py
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/base.py").readLines().drop(400).take(100).mkString("\n")
)
