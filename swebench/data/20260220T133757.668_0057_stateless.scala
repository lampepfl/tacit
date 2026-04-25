requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo") {
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo/django/db/models/base.py"
  val content = access(filePath).read()
  // Find all lines that contain _check_default_pk and print them with context
  val lines = content.split("\n")
  lines.zipWithIndex.filter((line, idx) => line.contains("_check_default_pk") || (line.contains("return []") && idx > 1295 && idx < 1330)).map((line, idx) => s"$idx: $line").mkString("\n")
}(using implicitly[IOCapability])