requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo/tests", "order_by.*root_id", "*.py")
  println(s"Found ${matches.size} matches")
}