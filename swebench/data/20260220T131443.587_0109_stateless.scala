
val content = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/fields/__init__.py").read())
val lines = content.split("\n").toList

// Add enum import
val importAdded = lines.updated(1, "import enum")

// Find to_python method
var toPythonStart = -1
importAdded.zipWithIndex.foreach { case (line, idx) =>
  if (line.contains("def to_python(self, value):") && idx > 570 && idx < 580) {
    toPythonStart = idx
  }
}

println(s"to_python starts at line $toPythonStart")
println(s"Content around that line: ${importAdded.drop(toPythonStart).take(6).mkString("\n")}")
