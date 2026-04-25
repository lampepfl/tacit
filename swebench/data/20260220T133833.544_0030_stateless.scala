requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo/tests", "class.*Q.*Test", "*.py")
}