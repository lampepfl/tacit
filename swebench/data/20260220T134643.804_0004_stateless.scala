requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo/django/utils/decorators.py")
  val lines = f.readLines()
  // Find the line numbers around _multi_decorate function
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("def _wrapper") && idx > 30 && idx < 60 then
      println(s"${idx + 1}: $line")
    if line.contains("bound_method = partial") then
      println(s"${idx + 1}: $line")
    if line.contains("for dec in decorators:") && idx > 30 && idx < 60 then
      println(s"${idx + 1}: $line")
  }
}