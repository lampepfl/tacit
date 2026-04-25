requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  val c = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo/django/db/models/sql/compiler.py")
  val content = c.read()
  val oldLine = "        if field.is_relation and opts.ordering and getattr(field, 'attname', None) != name and name != 'pk':"
  val newLine = "        if field.is_relation and opts.ordering and getattr(field, 'attname', None) != pieces[-1] and pieces[-1] != 'pk':"
  val newContent = content.replace(oldLine, newLine)
  c.write(newContent)
  println("Fixed!")
}