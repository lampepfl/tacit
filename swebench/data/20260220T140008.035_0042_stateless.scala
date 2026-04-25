def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val result = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/django", "class RenameModel")
    result.take(20).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
  }
}
myOp