requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11001/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11001/repo/django/db/models/sql/compiler.py")
  val content = f.read()
  
  // Find the two occurrences we need to change
  // Line 355: without_ordering = self.ordering_parts.search(sql).group(1)
  // Line 368: without_ordering = self.ordering_parts.search(sql).group(1)
  
  // Replace both occurrences
  val newContent = content
    .replace("without_ordering = self.ordering_parts.search(sql).group(1)", 
              "without_ordering = self.ordering_parts.search(' '.join(sql.splitlines())).group(1)")
  
  f.write(newContent)
  println("File updated successfully")
}