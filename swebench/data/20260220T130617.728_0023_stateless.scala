
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo") {
  val expr = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo/django/db/models/expressions.py")
  val content = expr.read()
  
  val oldCode = """    def get_group_by_cols(self, alias=None):
        return [self]"""

  val newCode = """    def get_group_by_cols(self, alias=None):
        return self.source.get_group_by_cols(alias=self.refs)"""

  val newContent = content.replace(oldCode, newCode)
  expr.write(newContent)
  println("Fixed!")
}
