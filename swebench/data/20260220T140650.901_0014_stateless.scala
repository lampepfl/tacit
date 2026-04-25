requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val base = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/django/core/management/base.py")
  val content = base.read()
  // Read lines around DjangoHelpFormatter class definition (lines 1-15)
  val lines = content.split("\n")
  for (i <- 0 until Math.min(20, lines.length)) {
    println(s"${i+1}: ${lines(i)}")
  }
}