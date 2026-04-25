requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  val tests = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/tests/ordering/tests.py")
  val lines = tests.readLines()
  lines.drop(460).take(40).zipWithIndex.foreach { case (line, i) => println(s"${i+460}: $line") }
}