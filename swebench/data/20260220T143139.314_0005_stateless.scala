requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo/django/forms/widgets.py")
  val lines = file.readLines()
  // Print lines around 267-280
  lines.slice(267, 282).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 267}: $line")
  }
}