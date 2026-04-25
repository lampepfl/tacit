
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo/django/db/models/fields/__init__.py")
  val content = file.read()
  // Let me try with proper tab handling
  val oldCode = "\tif self.choices is not None:\n\t            setattr(cls, 'get_%s_display' % self.name,\n\t                    partialmethod(cls._get_FIELD_display, field=self))"
  println(s"Looking for: $oldCode")
  val newCode = "\tif self.choices is not None:\n\t            if 'get_%s_display' % self.name not in cls.__dict__:\n\t                setattr(cls, 'get_%s_display' % self.name,\n\t                        partialmethod(cls._get_FIELD_display, field=self))"
  val updatedContent = content.replace(oldCode, newCode)
  if updatedContent == content then
    println("No replacement made - trying different approach")
  else
    file.write(updatedContent)
    println("Done")
}
