
// Let's get the lines and modify them
val lines = fs.access("django/db/models/fields/__init__.py").readLines()

// Add enum import after collections.abc (line 0)
val newLines = lines.zipWithIndex.map { (line, idx) =>
  if (idx == 0) Seq("import collections.abc", "import enum").mkString("\n")
  else line
}

// Find and modify to_python method
val toPythonModified = newLines.zipWithIndex.map { (line, idx) =>
  if (line.contains("def to_python(self, value):") && idx > 570 && idx < 580) {
    // Found the to_python method, replace it
    """    def to_python(self, value):
        """
        Convert the input value into the expected Python data type, raising
        django.core.exceptions.ValidationError if the data can't be converted.
        Return the converted value. Subclasses should override this.
        """
        if isinstance(value, enum.Enum):
            return value.value
        return value"""
  } else line
}

// Join back to string
val modifiedContent = toPythonModified.mkString("\n")

// Write the file
fs.access("django/db/models/fields/__init__.py").write(modifiedContent)

"Done"
