
// Search for _save_table method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/base.py", "def _save_table").take(5).mkString("\n")
)
