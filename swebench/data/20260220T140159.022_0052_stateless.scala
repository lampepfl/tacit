def myOp(using io: IOCapability) = {
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") {
    val result = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo/tests", "RenameModel", "*.py")
    println(s"Found ${result.size} matches")
    result.take(10).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
  }
}
myOp