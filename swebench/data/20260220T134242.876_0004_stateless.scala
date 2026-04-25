requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14608/repo") {
  val forms = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14608/repo/django/forms/forms.py")
  val content = forms.read()
  // Search for nonfield class
  val lines = content.split("\n").zipWithIndex
  lines.foreach { case (line, idx) =>
    if line.contains("nonfield") then
      println(s"${idx + 1}: $line")
  }
}
