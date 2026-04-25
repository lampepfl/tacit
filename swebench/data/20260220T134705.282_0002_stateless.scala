
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo") {
  val fs: FileSystem^ = summon
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo/django/contrib/admin/helpers.py")
  val lines = file.readLines()
  // Print lines 180-220 to see the class definition
  lines.drop(179).take(45).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 180}: $line")
  }
}
