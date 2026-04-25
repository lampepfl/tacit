requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/tests/model_fields/tests.py")
  val lines = file.readLines()
  
  // Search for E330 (unique check)
  lines.zipWithIndex.filter { case (line, _) => line.contains("E330") }.foreach { case (line, i) =>
    println(s"${i + 1}: $line")
  }
}
