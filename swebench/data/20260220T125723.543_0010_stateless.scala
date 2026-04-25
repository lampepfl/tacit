
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo/django/db/models/query.py")
  val lines = f.readLines()
  // Print lines around 1390-1410
  lines.drop(1389).take(25).zipWithIndex.foreach { case (line, i) =>
    println(s"${1390 + i}: $line")
  }
}
