requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  
  // Simple find and replace for the specific line
  val oldLine = "                return \"%s.%s\" % (module, self.value.__name__), {\"import %s\" % module}"
  val newLine = "                return \"%s.%s\" % (module, self.value.__qualname__), {\"import %s\" % module}"
  
  // First, let's check if there are two occurrences
  val count = content.sliding(oldLine.length).count(s => s == oldLine)
  println(s"Found $count occurrences of the old line")
  
  val newContent = content.replace(oldLine, newLine)
  serializer.write(newContent)
  println("Done!")
}