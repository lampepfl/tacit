requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo/django/utils/text.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Line 405 should be modified to also strip underscores
  // Current: value = re.sub(r'[^\w\s-]', '', value.lower()).strip()
  // New: We need to also include underscores in the character class to strip them
  // Actually, we want to keep underscores as valid characters but convert them to hyphens
  
  // Looking at line 406, we need to add underscores to the replacement pattern
  // Current: return re.sub(r'[-\s]+', '-', value)
  // New: return re.sub(r'[-\s_]+', '-', value).strip('-')
  
  val newLines = lines.updated(405, "value = re.sub(r'[^\\w\\s-]', '', value.lower()).strip()").updated(406, "return re.sub(r'[-\\s_]+', '-', value).strip('-')")
  
  f.write(newLines.mkString("\n"))
  println("Done!")
}