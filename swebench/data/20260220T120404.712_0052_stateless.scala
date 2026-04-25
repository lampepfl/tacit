requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  
  // Use the exact spacing
  val spaces16 = " " * 16
  val spaces20 = " " * 20
  val spaces12 = " " * 12
  
  val oldText = s"${spaces16}except ValueError:\n${spaces20}return None\n${spaces12}return path[match.end():], (), kwargs"
  val newText = s"${spaces16}except ValueError:\n${spaces20}return None\n${spaces16}except Http404:\n${spaces16}raise Resolver404()\n${spaces12}return path[match.end():], (), kwargs"
  
  val newContent = content.replace(oldText, newText)
  if newContent == content then
    println("Still no change")
  else
    resolvers.write(newContent)
    println("Changed")
}