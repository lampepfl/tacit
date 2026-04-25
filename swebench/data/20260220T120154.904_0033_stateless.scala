requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  
  // Fix the broken indentation
  val brokenCode = """                try:
                      kwargs[key] = converter.to_python(value)
                  except ValueError:
                      return None
                  except Http404:
                      raise Resolver404()
              return path[match.end():], (), kwargs"""
  val fixedCode = """                try:
                    kwargs[key] = converter.to_python(value)
                except ValueError:
                    return None
                except Http404:
                    raise Resolver404()
            return path[match.end():], (), kwargs"""
  
  val newContent = content.replace(brokenCode, fixedCode)
  if newContent == content then
    println("No fix made - trying different approach")
  else
    resolvers.write(newContent)
    println("Fixed indentation")
}