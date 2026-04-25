requestFileSystem[Unit]("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo") { (fs: FileSystem) ?=>
  // Let's look for tests related to empty queryset deletion
  val testsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo/tests/delete/tests.py")
  val lines = testsFile.readLines()
  
  // Search for tests that check delete result
  val results = lines.zipWithIndex.filter { case (line, idx) => 
    line.contains("delete") && (line.contains("Counter") || line.contains("tuple") || line.contains("dict"))
  }
  results.take(20).foreach { case (line, idx) => println(s"${idx + 1}: $line") }
}