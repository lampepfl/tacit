
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12700/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  // Find test files related to debug or cleanse_setting
  val testFiles = find("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12700/repo", "*test*debug*.py")
  testFiles.foreach(println)
}
