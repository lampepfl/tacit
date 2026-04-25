
// Read around line 722 in __init__.py
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/fields/__init__.py").readLines().drop(715).take(30).mkString("\n")
)
