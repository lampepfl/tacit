requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  // Look at the existing tests for inline verbose_name
  val matches = grepRecursive("tests", "verbose_name.*inline", "*.py")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}