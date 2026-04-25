requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo/tests/migrations/test_autodetector.py")
  val lines = f.readLines()
  // Print lines around 835-870
  lines.slice(833, 870).zipWithIndex.foreach { case (line, i) => println(s"${i + 833}: $line") }
}