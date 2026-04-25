requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  // Find the formfield method - let's search for it
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("def formfield") && idx > 900 && idx < 1100 then
      println(s"${idx + 1}: $line")
  }
}