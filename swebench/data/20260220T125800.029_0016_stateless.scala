
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo/django/db/models/query.py")
  val lines = f.readLines()
  // Print lines around 985-1010 
  lines.drop(984).take(30).zipWithIndex.foreach { case (line, i) =>
    println(s"${985 + i}: $line")
  }
}
