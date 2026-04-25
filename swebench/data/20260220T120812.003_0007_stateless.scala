requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo") {
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo/tests", "enum", "*.py")
  matches.take(30).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}