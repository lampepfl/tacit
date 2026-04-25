requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11001/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11001/repo/django/db/models/sql/compiler.py")
  val lines = f.readLines()
  
  // Check around line 355
  println("=== Lines around 350-362 ===")
  for (i <- 350 to 362) {
    println(s"$i: ${lines(i)}")
  }
  println()
  
  // Check around line 368
  println("=== Lines around 365-375 ===")
  for (i <- 365 to 375) {
    println(s"$i: ${lines(i)}")
  }
}