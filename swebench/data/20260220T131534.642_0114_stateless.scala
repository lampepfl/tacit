
// Find the lines in base.py where values are set
val content = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/base.py").read())
val lines = content.split("\n").toList

// Find the lines with "_setattr(self, field.attname, val)"
var setattrLines = List[Int]()
lines.zipWithIndex.foreach { case (line, idx) =>
  if (line.contains("_setattr(self, field.attname, val)")) {
    setattrLines = idx :: setattrLines
  }
}

setattrLines.sorted.mkString(", ")
