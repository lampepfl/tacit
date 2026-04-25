
// Search for the create method in QuerySet
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/query.py", "def create").take(5).mkString("\n")
)
