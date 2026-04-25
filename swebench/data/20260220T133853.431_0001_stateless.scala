
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14238/repo") {
  val result = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14238/repo/django", "AutoFieldMeta", "*.py")
  result.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
