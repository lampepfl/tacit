requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  // Search for tests related to m2m checks in the whole tests directory
  val testFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/tests/model_fields/tests.py")
  val lines = testFile.readLines()
  
  // Look for ManyToMany related check tests
  lines.zipWithIndex.filter { case (line, _) => 
    line.contains("ManyToMany") && (line.contains("check") || line.contains("Error") || line.contains("Warning"))
  }.foreach { case (line, i) =>
    println(s"${i + 1}: $line")
  }
}
