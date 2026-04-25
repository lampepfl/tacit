
// Look at the context around line 484
val content = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/base.py").read())
val lines = content.split("\n").toList

lines.drop(475).take(30).mkString("\n")
