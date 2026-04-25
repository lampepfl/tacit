requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  // Look for ManyToManyField formfield
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  // Find ManyToManyField class
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("class ManyToManyField") then
      println(s"${idx + 1}: $line")
  }
}