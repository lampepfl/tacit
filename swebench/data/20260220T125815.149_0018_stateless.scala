
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo/django/db/models/sql/compiler.py")
  val lines = f.readLines()
  // Print lines around 488-530
  lines.drop(487).take(50).zipWithIndex.foreach { case (line, i) =>
    println(s"${488 + i}: $line")
  }
}
