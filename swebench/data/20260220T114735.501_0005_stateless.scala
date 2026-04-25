requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11001/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11001/repo/django/db/models/sql/compiler.py")
  val lines = f.readLines()
  // Print lines around 363-375 for get_extra_select
  for (i <- 360 to 375) {
    println(s"$i: ${lines(i)}")
  }
}