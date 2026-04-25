requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  val f = access("tests/admin_inlines/models.py")
  val lines = f.readLines()
  // Show lines 340-360
  lines.slice(339, 365).zipWithIndex.foreach { case (line, i) => println(s"${i+340}: $line") }
}