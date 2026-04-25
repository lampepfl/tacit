
// Look for CharField's to_python method
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/fields/__init__.py").readLines().drop(1010).take(30).mkString("\n")
)
