
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo") {
  val fs: FileSystem^ = summon
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo/django/contrib/admin/helpers.py")
  val lines = file.readLines()
  // Print lines 160-180 to see the class definition
  lines.drop(159).take(25).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 160}: $line")
  }
}
