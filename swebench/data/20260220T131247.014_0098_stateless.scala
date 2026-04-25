
// Let's read around line 575 to see the exact location of to_python
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/fields/__init__.py").readLines().drop(570).take(20).mkString("\n")
)
