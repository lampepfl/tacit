requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo/django/db/migrations/autodetector.py")
  val lines = f.readLines()
  // Print lines around 925-960
  lines.slice(925, 962).zipWithIndex.foreach { case (line, i) => println(s"${i + 925}: $line") }
}