
// Search for the save method in Model
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/base.py", "def save").take(10).mkString("\n")
)
