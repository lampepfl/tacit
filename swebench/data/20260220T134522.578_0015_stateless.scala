requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/reverse_related.py")
  val lines = file.readLines()
  // Print from line 280 onwards
  lines.drop(279).take(30).zipWithIndex.foreach { case (line, i) =>
    println(s"${280 + i}: $line")
  }
}
