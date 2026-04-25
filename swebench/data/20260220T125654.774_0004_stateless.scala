requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo/django/utils/text.py")
  val lines = f.readLines()
  lines.slice(400, 410).zipWithIndex.foreach { case (line, idx) => println(s"${idx + 401}: $line") }
}