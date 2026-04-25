requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo") { 
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo/django/db/models/fields/reverse_related.py"
  val content = access(filePath).read()
  
  // Find and print the ManyToManyRel.identity section
  val start = content.indexOf("class ManyToManyRel")
  val end = content.indexOf("def get_related_field", start)
  println(content.substring(start, end))
}
