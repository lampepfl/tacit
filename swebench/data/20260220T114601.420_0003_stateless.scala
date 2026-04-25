requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11001/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11001/repo/django/db/models/sql/compiler.py")
  val lines = f.readLines()
  // Print lines around 35 (ordering_parts definition)
  println("=== Lines around 35 (ordering_parts definition) ===")
  for (i <- 30 to 45) {
    println(s"$i: ${lines(i)}")
  }
  println()
  // Print lines around 356 (first usage)
  println("=== Lines around 350-380 (get_order_by method) ===")
  for (i <- 340 to 380) {
    println(s"$i: ${lines(i)}")
  }
}