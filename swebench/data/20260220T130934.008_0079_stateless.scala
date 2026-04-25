
// Continue reading the save method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/base.py").readLines().drop(710).take(50).mkString("\n")
)
