
// Read the _save_table method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/base.py").readLines().drop(828).take(80).mkString("\n")
)
