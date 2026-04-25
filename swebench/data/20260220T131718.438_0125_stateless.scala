
val x = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/fields/__init__.py").readLines().drop(1010).take(15).mkString("\n"))
x
