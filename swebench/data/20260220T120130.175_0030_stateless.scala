requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Try with 16 spaces (4 levels of indentation)
  val oldCode = "········try:\n············kwargs[key] = converter.to_python(value)\n········except ValueError:\n············return None\n········return path[match.end():], (), kwargs"
  val newCode = "········try:\n············kwargs[key] = converter.to_python(value)\n········except ValueError:\n············return None\n········except Http404:\n············raise Resolver404()\n········return path[match.end():], (), kwargs"
  val newContent = content.replace(oldCode, newCode)
  if newContent == content then
    println("No change made - old code not found")
  else
    resolvers.write(newContent)
    println("Match method updated")
}