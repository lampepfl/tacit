requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  val lines = content.split("\n")
  
  // Remove the duplicate line (270) and keep the fixed one (271)
  // Actually let's just rewrite from line 265 onwards to fix the issue
  
  // First let's restore the original and make the correct change
  val lines2 = content.split("\n")
  // Fix line 270 and 271 - line 270 should be removed, line 271 should be fixed
  val newLines = scala.collection.mutable.ArrayBuffer[String]()
  var idx = 0
  while idx < lines2.length do
    if idx == 270 then
      // Skip this line (the broken one)
      newLines += "                return \"%s.%s\" % (module, self.value.__qualname__), {\"import %s\" % module}"
    else if idx == 269 then
      newLines += lines2(idx)
    else if idx == 271 then
      // Skip this line too (the old one)
    else
      newLines += lines2(idx)
    idx += 1
  
  val newContent = newLines.mkString("\n")
  serializer.write(newContent)
  println("Fixed!")
}