requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14608/repo") {
  val formsets = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14608/repo/django/forms/formsets.py")
  val lines = formsets.readLines()
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("self.error_class()") || line.contains("self._non_form_errors") then
      println(s"${idx + 1}: $line")
  }
}
