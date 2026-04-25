requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo") {
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo/django/db/models/base.py"
  val content = access(filePath).read()
  val lines = content.split("\n")
  // Find the line with _check_default_pk
  val idx = lines.indexWhere(_.contains("def _check_default_pk"))
  // Print lines from idx-2 to idx+35
  lines.slice(idx-2, idx+35).mkString("\n")
}(using implicitly[IOCapability])