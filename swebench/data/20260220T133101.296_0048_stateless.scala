requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo") {
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo/django/db/models/base.py"
  val content = access(filePath).read()
  // Find all occurrences of _check_default_pk
  var idx = content.indexOf("def _check_default_pk")
  val result = scala.collection.mutable.ListBuffer[String]()
  while (idx >= 0) {
    // Get context before and after
    val start = Math.max(0, idx - 200)
    val end = Math.min(content.length, idx + 600)
    result.addOne(content.substring(start, end))
    idx = content.indexOf("def _check_default_pk", idx + 1)
  }
  result.toList
}(using implicitly[IOCapability])