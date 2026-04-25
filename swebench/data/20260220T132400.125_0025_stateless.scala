requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  val f = access("django/contrib/admin/options.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Make the change: replace lines 2042-2043 (0-indexed: 2041-2042)
  // Old:
  //         if self.verbose_name_plural is None:
  //             self.verbose_name_plural = self.model._meta.verbose_name_plural
  // New:
  //         if self.verbose_name_plural is None:
  //             self.verbose_name_plural = format_lazy('{}s', self.verbose_name)
  
  val newLines = lines.updated(2042, "            self.verbose_name_plural = format_lazy('{}s', self.verbose_name)")
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated successfully")
}