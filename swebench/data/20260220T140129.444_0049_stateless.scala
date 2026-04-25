def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/django/db/migrations/operations/models.py")
    val content = f.read()
    // Find the line number for database_forwards in RenameModel
    val lines = content.split("\n")
    val startLine = lines.indexOf("    def database_forwards(self, app_label, schema_editor, from_state, to_state):")
    println(s"database_forwards starts at line: $startLine")
    // Print from line 319 (index 318) to see the current content
    lines.slice(318, 325).zipWithIndex.foreach { case (line, idx) =>
      println(s"${idx + 319}: $line")
    }
  }
}
myOp