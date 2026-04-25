requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  
  // Use 16-space indentation
  val oldText = "················except ValueError:\n····················return None\n············return path[match.end():], (), kwargs"
  val newText = "················except ValueError:\n····················return None\n················except Http404:\n····················raise Resolver404()\n············return path[match.end():], (), kwargs"
  
  val newContent = content.replace(oldText, newText)
  if newContent == content then
    println("Still no change")
  else
    resolvers.write(newContent)
    println("Changed")
}