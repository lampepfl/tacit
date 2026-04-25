requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Find URLResolver class and its resolve method
  val lines = content.split("\n")
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("class URLResolver") || (line.contains("def resolve") && idx > 300 && idx < 500) then
      println(s"${idx + 1}: $line")
  }
}