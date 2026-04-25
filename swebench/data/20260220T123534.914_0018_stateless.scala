
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo/django/db/models/fields/related.py")
  var content = f.read()
  
  // More precise pattern - look for just the inner line that has the ForeignKey mention
  val oldLine = """'use ForeignKey("%s", symmetrical=False, through="%s").'"""
  val newLine = """'use ManyToManyField("%s", through="%s").'"""
  
  content = content.replace(oldLine, newLine)
  
  f.write(content)
  
  // Verify
  val newContent = f.read()
  val lines = newContent.split("\n")
  lines.zipWithIndex.filter((line, idx) => line.contains("ManyToManyField") && line.contains("recursive")).foreach((line, idx) => println(s"${idx+1}: $line"))
}
