requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  // Search for property ordering in options.py
  val options = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django/db/models/options.py")
  val lines = options.readLines()
  // Look for @property ordering
  lines.zipWithIndex.filter(_._1.contains("@property") && lines(zipWithIndex.map(_+1).getOrElse(idx+1, "")).exists(_.contains("ordering"))).foreach { case (line, idx) =>
    println(s"${idx+1}: $line")
  }
}