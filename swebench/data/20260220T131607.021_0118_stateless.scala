
val content = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/base.py").read())
val lines = content.split("\n").toList

// Find all lines containing the pattern
lines.zipWithIndex.filter { case (line, idx) => 
  line.contains("_setattr(self, field.attname, val)")
}.map(_._2).sorted
