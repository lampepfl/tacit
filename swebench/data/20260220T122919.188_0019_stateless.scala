requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  // Find tests related to admin utils
  val testMatches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/tests", "display_for_field", "*.py")
  testMatches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
