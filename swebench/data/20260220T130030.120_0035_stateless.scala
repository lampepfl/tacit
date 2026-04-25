
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo/django/forms/models.py")
  val lines = f.readLines()
  // Print lines around 1295-1360
  lines.drop(1294).take(70).zipWithIndex.foreach { case (line, i) =>
    println(s"${1295 + i}: $line")
  }
}
