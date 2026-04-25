
val x = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/base.py").readLines().drop(430).take(20).mkString("\n"))
x
