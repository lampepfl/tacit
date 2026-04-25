requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Get the exact bytes
  val idx = content.indexOf("except ValueError:")
  val slice = content.slice(idx - 10, idx + 80)
  // Convert to list of int codes
  slice.map(c => (c.toInt, c)).foreach(println)
}