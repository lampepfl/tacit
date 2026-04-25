requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Replace exact lines
  val oldCode = """                try:
                    kwargs[key] = converter.to_python(value)
                except ValueError:
                    return None
            return path[match.end():], (), kwargs"""
  val newCode = """                try:
                    kwargs[key] = converter.to_python(value)
                except ValueError:
                    return None
                except Http404:
                    raise Resolver404()
            return path[match.end():], (), kwargs"""
  val newContent = content.replace(oldCode, newCode)
  if newContent == content then
    println("No change made - old code not found")
  else
    resolvers.write(newContent)
    println("Match method updated")
}