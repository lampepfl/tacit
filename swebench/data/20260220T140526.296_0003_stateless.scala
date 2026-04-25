requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  // Search for DjangoHelpFormatter
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/django/core/management", "DjangoHelpFormatter")
  matches.foreach { m =>
    println(s"${m.file}:${m.lineNumber}: ${m.line}")
  }
}