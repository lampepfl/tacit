requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  
  // The line at index 270 (271st line) has the bug - change __name__ to __qualname__
  val lines = content.split("\n")
  // Check line 271 - index 270
  println("Original line 271: " + lines(270))
  
  // Replace the specific line
  lines(270) = "                return \"%s.%s\" % (module, self.value.__qualname__), {\"import %s\" % module}"
  
  val newContent = lines.mkString("\n")
  serializer.write(newContent)
  println("Done!")
}