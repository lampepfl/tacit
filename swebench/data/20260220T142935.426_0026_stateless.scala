requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  val lines = content.split("\n")
  
  // Fix line 271 (index 270)
  // The line is: "                return \"%s.%s\" % (module, self.value.__name__), {\"import %s\" % module}"
  // Change it to use __qualname__
  lines(270) = "                return \"%s.%s\" % (module, self.value.__qualname__), {\"import %s\" % module}"
  
  val newContent = lines.mkString("\n")
  serializer.write(newContent)
  println("Done!")
}