
val content = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/fields/__init__.py").read())
val lines = content.split("\n").toList

// Add enum import after line 0
val importAdded = lines.updated(1, "import enum")

// Find to_python method - we found it's at line 574 (0-indexed)
val toPythonStart = 574

// Create the new to_python method as a list of strings
val newToPython = List(
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

// Replace the lines (7 lines from toPythonStart)
val finalLines = importAdded.patch(toPythonStart, newToPython, 7)

// Write back
val modifiedContent = finalLines.mkString("\n")

requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/fields/__init__.py").write(modifiedContent))

"Done"
