def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/tests/migrations/test_operations.py")
    val lines = f.readLines()
    // Find the RenameModel test section
    val startIdx = lines.indexWhere(_.contains("def test_rename_model"))
    println(s"Found test_rename_model at line: $startIdx")
    // Print the next 100 lines to see the tests
    lines.slice(startIdx, startIdx + 100).zipWithIndex.foreach { case (line, idx) =>
      println(s"${idx + startIdx + 1}: $line")
    }
  }
}
myOp