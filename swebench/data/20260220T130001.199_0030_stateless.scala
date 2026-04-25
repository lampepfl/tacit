
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo/tests/queries/test_qs_combinators.py")
  val lines = f.readLines()
  // Print lines around 290-320 
  lines.drop(288).take(35).zipWithIndex.foreach { case (line, i) =>
    println(s"${289 + i}: $line")
  }
}
