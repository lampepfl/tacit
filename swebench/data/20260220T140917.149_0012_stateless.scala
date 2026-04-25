requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo") {
  val f = access("django/utils/functional.py")
  val lines = f.readLines()
  // Get lines around SimpleLazyObject class definition
  lines.drop(379).take(100).zipWithIndex.map { case (line, idx) => s"${384 + idx + 1}: $line" }
}