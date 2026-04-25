
// Read the code around line 1461 in query.py
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/query.py").readLines().drop(1455).take(30).mkString("\n")
)
