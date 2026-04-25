
// Let's look at Model.__init__ again, specifically for value conversion
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/base.py").readLines().drop(418).take(60).mkString("\n")
)
