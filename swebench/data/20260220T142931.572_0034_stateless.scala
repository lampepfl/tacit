requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo") {
  // Make the fix - change line 730 to use 'ASC' instead of 'order'
  val compiler = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12470/repo/django/db/models/sql/compiler.py")
  val lines = compiler.readLines().toList
  // Update line 730 (index 729) to use 'ASC' instead of 'order'
  val newLines = lines.updated(729, "                                                       order, already_seen)")
  // Actually, let me look at the exact text to replace
  println(s"Original line 730: ${lines(729)}")
}