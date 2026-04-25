requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  val options = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django/db/models/options.py")
  val lines = options.readLines()
  // Search for ordering related code - more context
  lines.zipWithIndex.filter(_._1.contains("ordering")).take(50).foreach { case (line, idx) =>
    println(s"${idx+1}: $line")
  }
}