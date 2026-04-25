requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Get all lines that contain "def match"
  val lines = content.split("\n")
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("def match") then
      println(s"${idx + 1}: $line")
  }
}