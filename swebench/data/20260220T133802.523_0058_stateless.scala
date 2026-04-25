requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo") {
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo/django/db/models/base.py"
  val content = access(filePath).read()
  val lines = content.split("\n")
  // Print lines from 1290 to 1325
  lines.slice(1288, 1325).zipWithIndex.map((line, idx) => s"${idx+1289}: $line").mkString("\n")
}(using implicitly[IOCapability])