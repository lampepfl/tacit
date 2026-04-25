requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/django/db/models/fields/__init__.py")
  val content = f.read()
  
  val oldCode = """        if self.choices is not None:
            if not hasattr(cls, 'get_%s_display' % self.name):
                setattr(
                    cls,
                    'get_%s_display' % self.name,
                    partialmethod(cls._get_FIELD_display, field=self),
                )"""

  val newCode = """        if self.choices is not None:
            if 'get_%s_display' % self.name not in cls.__dict__:
                setattr(
                    cls,
                    'get_%s_display' % self.name,
                    partialmethod(cls._get_FIELD_display, field=self),
                )"""
  
  val newContent = content.replace(oldCode, newCode)
  f.write(newContent)
  println("Done")
}