requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo/django/forms/widgets.py")
  val lines = file.readLines()
  // Print lines around 268 (Widget's id_for_label)
  lines.slice(265, 275).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 265}: $line")
  }
}