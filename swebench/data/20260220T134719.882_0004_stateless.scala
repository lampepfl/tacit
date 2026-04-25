
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo") {
  val fs: FileSystem^ = summon
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo/django/contrib/admin/helpers.py")
  val content = file.read()
  // Get the exact content around the bug
  val lines = content.split("\n")
  println(s"Line 206-215:")
  lines.drop(205).take(10).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 206}: $line")
  }
}
