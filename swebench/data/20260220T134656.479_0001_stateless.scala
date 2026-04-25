
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo") {
  val fs: FileSystem^ = summon
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo/django/contrib/admin/helpers.py")
  val lines = file.readLines()
  // Print lines 195-220 to see the get_admin_url function
  lines.drop(194).take(30).zipWithIndex.foreach { case (line, idx) =>
    println(s"${idx + 195}: $line")
  }
}
