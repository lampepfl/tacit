requestFileSystem[Unit]("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo") { (fs: FileSystem) ?=>
  // Let's find deletion tests
  val results = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo/tests", "delete", "*.py")
  // Filter for deletion test files
  val deletionTests = results.filter(m => m.file.contains("delete"))
  deletionTests.take(10).foreach(m => println(s"${m.file}"))
}