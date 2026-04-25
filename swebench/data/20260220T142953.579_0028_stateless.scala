requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  // Restore again first
  requestExecPermission(Set("git")) {
    exec("git", List("checkout", "--", "django/db/migrations/serializer.py"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo"))
  }
  
  // Now let's carefully read the content and find the exact line
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  
  // Use a simple string replacement instead
  val target = "return \"%s.%s\" % (module, self.value.__name__), {\"import %s\" % module}"
  val replacement = "return \"%s.%s\" % (module, self.value.__qualname__), {\"import %s\" % module}"
  
  // Find where this occurs
  val idx = content.indexOf(target)
  println(s"Found target at index: $idx")
  
  if idx > 0 then
    val newContent = content.substring(0, idx) + replacement + content.substring(idx + target.length)
    serializer.write(newContent)
    println("Done!")
}