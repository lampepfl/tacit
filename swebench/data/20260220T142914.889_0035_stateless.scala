requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/django/db/models/fields/__init__.py")
  val content = f.read()
  
  val newContent = content.replace(
    "if not hasattr(cls, 'get_%s_display' % self.name):",
    "if 'get_%s_display' % self.name not in cls.__dict__:"
  )
  
  f.write(newContent)
  println("Done")
}