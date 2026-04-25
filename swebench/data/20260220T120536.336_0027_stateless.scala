
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11630/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11630/repo/django/core/checks/model_checks.py")
  val lines = f.readLines()
  // Find the lines with our changes
  lines.zipWithIndex.foreach { case(line, idx) =>
    if (line.contains("router.db_for_write") || line.contains("db_table[0]") || line.contains("db_table_models[(model._meta.db_table")) {
      println(s"Line ${idx + 1}: $line")
    }
  }
}
