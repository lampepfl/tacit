
// Check if DeferredAttribute has a __set__ method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/query_utils.py", "__set__").take(5).mkString("\n")
)
