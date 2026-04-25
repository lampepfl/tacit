requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  // Find ManyToManyField formfield method
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("def formfield") && idx > 1280 then
      println(s"${idx + 1}: $line")
  }
}