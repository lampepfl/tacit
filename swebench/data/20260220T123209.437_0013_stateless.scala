requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  val options = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django/db/models/options.py")
  val lines = options.readLines()
  val idx = lines.indexWhere(_.contains("def get_ordering"))
  println(s"get_ordering at line $idx")
  if (idx >= 0) {
    lines.drop(idx).take(60).zipWithIndex.foreach { case (line, i) => println(s"${i+idx}: $line") }
  }
}