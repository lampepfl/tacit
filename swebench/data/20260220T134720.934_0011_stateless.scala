requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14017/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14017/repo/django/db/models/expressions.py")
  val lines = f.readLines()
  // Print first 50 lines to see imports
  for (i <- 0 to 40) {
    println(s"$i: ${lines(i)}")
  }
}