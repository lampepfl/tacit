
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14382/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14382/repo/django/core/management/templates.py")
  val content = file.read()
  
  // Find the specific line
  val lines = content.split("\n")
  lines.zipWithIndex.foreach { case (line, idx) =>
    if (line.contains("validate_name") && line.contains("directory")) {
      println(s"Line ${idx + 1}: $line")
    }
  }
}
