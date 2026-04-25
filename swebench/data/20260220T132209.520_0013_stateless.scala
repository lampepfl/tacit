requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  val f = access("django/contrib/admin/options.py")
  val lines = f.readLines()
  // Show lines 2010-2060
  lines.slice(2009, 2060).zipWithIndex.foreach { case (line, i) => println(s"${i+2010}: $line") }
}