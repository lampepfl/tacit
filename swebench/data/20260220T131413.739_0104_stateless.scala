
requestFileSystem(".")((fs: FileSystem^) ?=> 
// Let's do this differently - read line by line and modify
val lines = fs.access("django/db/models/fields/__init__.py").readLines()

// Add enum import after line 0 (import collections.abc)
val importAdded = lines.updated(1, "import enum")

// Now find and modify the to_python method
// We need to find the line with "def to_python(self, value):" around line 575
var toPythonStart = -1

importAdded.zipWithIndex.foreach { (line, idx) =>
  if (line.contains("def to_python(self, value):") && idx > 570 && idx < 580) {
    toPythonStart = idx
  }
}

// Replace lines from toPythonStart to toPythonStart+6
val newToPython = Seq(
  "    def to_python(self, value):",
  "        \"\"\"",
  "        Convert the input value into the expected Python data type, raising",
  "        django.core.exceptions.ValidationError if the data can't be converted.",
  "        Return the converted value. Subclasses should override this.",
  "        \"\"\"",
  "        if isinstance(value, enum.Enum):",
  "            return value.value",
  "        return value"
)

// Replace the lines
val finalLines = importAdded.patch(toPythonStart, newToPython, 7)

val modifiedContent = finalLines.mkString("\n")

fs.access("django/db/models/fields/__init__.py").write(modifiedContent)

"File updated"
)
