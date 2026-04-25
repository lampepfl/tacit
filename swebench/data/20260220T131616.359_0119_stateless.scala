
val content = requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/base.py").read())
val lines = content.split("\n").toList

// Replace line 433
val line433 = lines(433).replace("val)", "field.to_python(val))")

// Replace line 440
val line440 = lines(440).replace("val)", "field.to_python(val))")

// Replace line 484
val line484 = lines(484).replace("val)", "field.to_python(val))")

// Update the lines
val modified = lines.updated(433, line433).updated(440, line440).updated(484, line484)

// Write back
val modifiedContent = modified.mkString("\n")

requestFileSystem(".")((fs: FileSystem^) ?=> fs.access("django/db/models/base.py").write(modifiedContent))

"Done"
