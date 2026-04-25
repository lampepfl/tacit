requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  // Read formfield method
  val startLine = 983
  val endLine = 1060
  lines.slice(startLine, endLine).zipWithIndex.foreach { case (line, idx) =>
    println(s"${startLine + idx + 1}: $line")
  }
}