requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14238/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14238/repo/django/db/models/fields/__init__.py")
  val content = file.read()
  
  // The exact text to replace
  val oldText = "    def __subclasscheck__(self, subclass):\n        return subclass in self._subclasses or super().__subclasscheck__(subclass)"
  val newText = "    def __subclasscheck__(self, subclass):\n        return issubclass(subclass, self._subclasses) or super().__subclasscheck__(subclass)"
  
  val newContent = content.replace(oldText, newText)
  
  if (newContent == content) {
    println("ERROR: No changes made - text not found")
  } else {
    file.write(newContent)
    println("File updated successfully")
  }
}