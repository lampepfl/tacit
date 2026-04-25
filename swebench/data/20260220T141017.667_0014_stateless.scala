requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo") {
  val f = access("django/utils/functional.py")
  val lines = f.readLines()
  // Get LazyObject class - it ends where SimpleLazyObject starts (line 390)
  lines.drop(270).take(120).zipWithIndex.map { case (line, idx) => s"${270 + idx + 1}: $line" }
}