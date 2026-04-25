requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14017/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14017/repo/django/db/models/expressions.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Find where __rand__ starts (line 138) and __ror__ ends (line 146)
  // We need to replace lines 138-146 (inclusive, 0-indexed: 137-145)
  val before = lines.slice(0, 138).mkString("\n")
  val after = lines.slice(147, lines.length).mkString("\n")
  
  val newSection = """    def __rand__(self, other):
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
  
  val newContent = before + "\n" + newSection + "\n" + after
  f.write(newContent)
  println("File updated successfully")
}