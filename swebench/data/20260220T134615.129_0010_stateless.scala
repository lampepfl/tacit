requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14017/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14017/repo/django/db/models/expressions.py")
  val content = f.read()
  // Replace the __rand__ and __ror__ methods
  val newContent = content.replace(
    """    def __rand__(self, other):
        raise NotImplementedError(
            "Use .bitand() and .bitor() for bitwise logical operations."
        )

    def __ror__(self, other):
        raise NotImplementedError(
            "Use .bitand() and .bitor() for bitwise logical operations."
        )""",
    """    def __rand__(self, other):
        if getattr(self, 'conditional', False) and getattr(other, 'conditional', False):
            return Q(other) & Q(self)
        raise NotImplementedError(
            "Use .bitand() and .bitor() for bitwise logical operations."
        )

    def __ror__(self, other):
        if getattr(self, 'conditional', False) and getattr(other, 'conditional', False):
            return Q(other) | Q(self)
        raise NotImplementedError(
            "Use .bitand() and .bitor() for bitwise logical operations."
        )"""
  )
  f.write(newContent)
  println("File updated successfully")
}