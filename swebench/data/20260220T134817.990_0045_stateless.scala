requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val testDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/tests/model_fields")
  val testsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/tests/model_fields/tests.py")
  val lines = testsFile.readLines()
  lines.take(80).zipWithIndex.foreach { case (line, i) =>
    println(s"${i + 1}: $line")
  }
}
