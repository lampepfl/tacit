
val x = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/base.py").readLines().drop(480).take(10).mkString("\n"))
x
