requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo/tests/queries/test_qs_combinators.py")
  val lines = f.readLines()
  // Get lines 40-70 for context
  lines.slice(39, 75).zipWithIndex.map { case (line, idx) => s"${idx + 40}: $line" }
}