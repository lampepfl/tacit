
val x = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/fields/__init__.py").readLines().drop(573).take(12).mkString("\n"))
x
