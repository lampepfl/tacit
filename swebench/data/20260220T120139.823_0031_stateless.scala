requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Try with different spacing approach
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
  
  // First check if the string exists at all
  val testOld = "                try:\n                    kwargs[key] = converter.to_python(value)\n                except ValueError:\n                    return None"
  println(s"Test old exists: ${content.contains(testOld)}")
  
  val newContent = content.replace(testOld, newCode)
  if newContent == content then
    println("Still no change")
  else
    resolvers.write(newContent)
    println("Updated")
}