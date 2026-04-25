requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo") {
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo/django/db/models/base.py"
  val content = access(filePath).read()
  val idx = content.indexOf("@classmethod")
  val idx2 = content.indexOf("def _check_default_pk")
  // Check if @classmethod is before _check_default_pk
  (idx, idx2, idx > 0 && idx2 > idx && idx2 - idx < 50)
}(using implicitly[IOCapability])