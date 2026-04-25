requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo/django/utils/functional.py")
  val lines = file.readLines().toList
  // Remove the incorrectly added lines and fix
  val newLines = lines.patch(432, List(
    "        return copy.deepcopy(self._wrapped, memo)",
    "",
    "    def __radd__(self, other):",
    "        if self._wrapped is empty:",
    "            self._setup()",
    "        return other + self._wrapped"
  ), 0)
  file.write(newLines.mkString("\n"))
}