requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  val f = access("tests/admin_inlines/tests.py")
  val lines = f.readLines()
  // Show lines 965-1030
  lines.slice(964, 1030).zipWithIndex.foreach { case (line, i) => println(s"${i+965}: $line") }
}