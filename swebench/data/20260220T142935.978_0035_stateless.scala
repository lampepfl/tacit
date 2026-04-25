requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  val compilerPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django/db/models/sql/compiler.py"
  val compiler = access(compilerPath)
  val lines = compiler.readLines().toList
  
  // Find line 730 (index 729) and replace 'order' with 'ASC'
  val lineToChange = lines(729)
  val newLine = lineToChange.replace("order, already_seen", "'ASC', already_seen")
  val newLines = lines.updated(729, newLine)
  
  // Write back
  compiler.write(newLines.mkString("\n"))
  println("Fixed!")
  println(s"New line 730: ${newLines(729)}")
}