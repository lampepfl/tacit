requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Modify the match method to catch Http404
  val oldCode = """                try:
                    kwargs[key] = converter.to_python(value)
                except ValueError:
                    return None"""
  val newCode = """                try:
                    kwargs[key] = converter.to_python(value)
                except ValueError:
                    return None
                except Http404:
                    raise Resolver404()"""
  val newContent = content.replace(oldCode, newCode)
  resolvers.write(newContent)
  println("Match method updated")
}