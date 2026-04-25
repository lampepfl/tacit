
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo") {
  val fs = summon[FileSystem]
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo/tests", "get_.*_display", "*.py")
  matches.take(20).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
