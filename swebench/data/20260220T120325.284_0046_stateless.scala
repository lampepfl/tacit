requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  
  // Find the exact lines to modify
  val oldText = """                except ValueError:
                    return None
            return path[match.end():], (), kwargs"""
  
  val newText = """                except ValueError:
                    return None
                except Http404:
                    raise Resolver404()
            return path[match.end():], (), kwargs"""
  
  val newContent = content.replace(oldText, newText)
  if newContent == content then
    println("No change - trying different approach")
  else
    resolvers.write(newContent)
    println("Changed successfully")
}