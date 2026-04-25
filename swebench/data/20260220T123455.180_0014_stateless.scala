
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo/django/db/models/fields/related.py")
  var content = f.read()
  
  // The old hint text - need exact match
  val oldHint = """'If you want to create a recursive relationship, '
                                'use ForeignKey("%s", symmetrical=False, through="%s").'"""
  
  val newHint = """'If you want to create a recursive relationship, '
                                'use ManyToManyField("%s", through="%s").'"""
  
  content = content.replace(oldHint, newHint)
  
  // Write back
  f.write(content)
  
  // Verify the change
  val newContent = f.read()
  val count = newContent.split("ManyToManyField").length - 1
  println(s"Occurrences of 'ManyToManyField' in hint: $count")
}
