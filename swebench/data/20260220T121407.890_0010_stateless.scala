
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo/django/db/models/fields/__init__.py")
  val content = file.read()
  val oldCode = """        if self.choices is not None:
            setattr(cls, 'get_%s_display' % self.name,
                    partialmethod(cls._get_FIELD_display, field=self))"""
  val newCode = """        if self.choices is not None:
            if 'get_%s_display' % self.name not in cls.__dict__:
                setattr(cls, 'get_%s_display' % self.name,
                        partialmethod(cls._get_FIELD_display, field=self))"""
  val updatedContent = content.replace(oldCode, newCode)
  file.write(updatedContent)
  println("Done")
}
