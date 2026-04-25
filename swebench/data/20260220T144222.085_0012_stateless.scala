requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo/django/forms/widgets.py")
  val content = file.read()
  val lines = content.split("\n")
  
  // Show more context
  lines.slice(847, 862).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 847}: |$line|")
  }
}