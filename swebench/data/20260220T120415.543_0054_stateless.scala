requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  
  // Fix the indentation of raise Resolver404()
  val oldText = "                except Http404:\n                raise Resolver404()"
  val newText = "                except Http404:\n                    raise Resolver404()"
  
  val newContent = content.replace(oldText, newText)
  if newContent == content then
    println("No change")
  else
    resolvers.write(newContent)
    println("Fixed")
}