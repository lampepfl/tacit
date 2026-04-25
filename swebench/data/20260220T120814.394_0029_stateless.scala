requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo") { 
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo/tests", "filter.*id=.*\\[", "*.py")
  matches.take(10).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}