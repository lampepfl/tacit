def runFs(using fs: FileSystem): Unit =
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15738/repo/django/db/migrations", "unique_together", "*.py")
  println("Found " + matches.length + " matches")
  matches.take(30).foreach(m => println(m.file + ":" + m.lineNumber + ": " + m.line))

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15738/repo")(runFs)