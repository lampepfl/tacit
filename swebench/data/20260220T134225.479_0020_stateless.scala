requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14411/repo") { 
  val f = access("django/contrib/auth/forms.py")
  val lines = f.readLines()
  lines.slice(30, 60).zipWithIndex.foreach((line, idx) => println(s"${idx + 30}: $line"))
}
