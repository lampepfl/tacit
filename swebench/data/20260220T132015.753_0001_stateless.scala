requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo") { fs =>
  // First, let's find the ManagementUtility class
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo", "class ManagementUtility", "*.py")
  println(s"Found ${matches.size} matches")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
