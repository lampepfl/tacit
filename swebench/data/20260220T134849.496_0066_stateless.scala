import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

val content = exec("cat", List("./django/db/models/sql/query.py.bak")).stdout
val lines = content.split("\n")

// Replace line 2088 (index 2087) with the fix
lines(2087) = "            new_fields = existing.difference(field_names)"
val newContent = lines.mkString("\n")

// Write to file
exec("python3", List("-c", "open('./django/db/models/sql/query.py', 'w').write(" + newContent.map(c => if (c == '\n') "\\n" else if (c == '\'')"\\\\''").mkString("\"", "", "\"") + ")"))