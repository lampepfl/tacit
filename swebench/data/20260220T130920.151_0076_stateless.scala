
// Read the QuerySet.create method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/query.py").readLines().drop(437).take(30).mkString("\n")
)
