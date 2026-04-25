import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

val fixCode = """
        existing, defer = self.deferred_loading
        if defer:
            # Add to existing deferred names.
            self.deferred_loading = existing.union(field_names), True
        else:
            # Remove names from the set of any existing "immediate load" names.
            new_fields = existing.difference(field_names)
            # If the result is empty, switch to defer mode (defer everything).
            # Otherwise, stay in immediate loading mode with the remaining fields.
            self.deferred_loading = (new_fields, not new_fields)
"""

val content = exec("cat", List("./django/db/models/sql/query.py.bak")).stdout
val lines = content.split("\n")

// Find and replace the add_deferred_loading method
var in_method = false
var brace_count = 0
var start_line = -1
var end_line = -1

for (i <- 0 until lines.length) {
  if (lines(i).contains("def add_deferred_loading")) {
    start_line = i
  }
  if (start_line >= 0 && !in_method) {
    in_method = true
  }
  if (in_method) {
    if (lines(i).trim.startsWith("def ") && i > start_line) {
      end_line = i
      i = lines.length // break
    }
  }
}

println(s"Found method at lines $start_line to $end_line")

// Show the original lines
for (i <- start_line until Math.min(start_line + 15, lines.length)) {
  println(s"$i: ${lines(i))}")
}
"""

exec("python3", List("-c", fixCode))