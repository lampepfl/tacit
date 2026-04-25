
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12184/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12184/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  
  // Find and replace the problematic line
  val oldCode = "            args = () if kwargs else match.groups()"
  val newCode = "            args = () if kwargs else tuple(v for v in match.groups() if v is not None)"
  
  if (content.contains(oldCode)) {
    val newContent = content.replace(oldCode, newCode)
    resolvers.write(newContent)
    println("Fixed!")
  } else {
    println("Code not found")
  }
}
