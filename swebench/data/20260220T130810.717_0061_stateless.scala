
// Continue reading the Model __init__ method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/base.py").readLines().drop(450).take(100).mkString("\n")
)
