requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo/django/core/management") { 
  val initFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo/django/core/management/__init__.py")
  val content = initFile.read()
  // Search for ManagementUtility class definition
  val lines = content.split("\n")
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("class ManagementUtility") || line.contains("CommandParser") then
      println(s"${idx + 1}: $line")
  }
}
