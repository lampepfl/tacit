requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  val f = access("tests/admin_inlines/tests.py")
  val lines = f.readLines()
  // Show lines 1016-1050
  lines.slice(1015, 1055).zipWithIndex.foreach { case (line, i) => println(s"${i+1016}: $line") }
}