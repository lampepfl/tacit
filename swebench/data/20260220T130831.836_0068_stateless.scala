
// Read the DeferredAttribute class
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/query_utils.py").readLines().drop(114).take(50).mkString("\n")
)
