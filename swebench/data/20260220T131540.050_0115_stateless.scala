
// Look at the context around line 433
val content = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/base.py").read())
val lines = content.split("\n").toList

lines.drop(425).take(20).mkString("\n")
