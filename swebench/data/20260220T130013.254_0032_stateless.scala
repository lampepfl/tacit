
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo/tests/model_forms/tests.py")
  val lines = f.readLines()
  // Print lines around 1750-1770 
  lines.drop(1748).take(25).zipWithIndex.foreach { case (line, i) =>
    println(s"${1749 + i}: $line")
  }
}
