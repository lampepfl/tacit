requestFileSystem[Unit]("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo") { (fs: FileSystem) ?=>
  // Let's look at the tests.py file to see what tests exist
  val testsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo/tests/delete/tests.py")
  val lines = testsFile.readLines()
  
  // Print first 50 lines
  lines.slice(0, 50).zipWithIndex.foreach { case (line, idx) => println(s"${idx + 1}: $line") }
}