requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  val compiler = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django/db/models/sql/compiler.py")
  val lines = compiler.readLines()
  println(s"Total lines: ${lines.length}")
  // Print lines around 265 (get_order_by)
  lines.drop(260).take(80).zipWithIndex.foreach { case (line, i) => println(s"${i+260}: $line") }
}