
// Let's look at the full Model.__init__ to understand where to add the conversion
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/base.py").readLines().drop(403).take(100).mkString("\n")
)
