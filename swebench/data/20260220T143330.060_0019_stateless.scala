requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo/django/utils/functional.py")
  val lines = file.readLines().toList
  
  // Find the indices of the duplicate __radd__ methods and the stray return statement
  // Remove lines 434-449 (the duplicates and stray line) and keep just one correct __radd__
  val newLines = lines.patch(433, List(
    "",
    "    def __radd__(self, other):",
    "        if self._wrapped is empty:",
    "            self._setup()",
    "        return other + self._wrapped"
  ), 0)
  file.write(newLines.mkString("\n"))
}