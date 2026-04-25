
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13158/repo/django/db/models/sql/compiler.py")
  val lines = f.readLines().toVector
  // Modify line 506 (index 505) to add the is_empty check
  // We need to insert after line 506: "                    raise NotSupportedError('{} is not supported on this database backend.'.format(combinator))"
  // and before line 507: "                result, params = self.get_combinator_sql(combinator, self.query.combinator_all)"
  
  val newLines = lines.updated(506, "                if self.query.is_empty():\n                    raise EmptyResultSet\n" + lines(506))
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated successfully")
}
