requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13551/repo") {
  grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13551/repo/tests", "PasswordResetTokenGenerator", "*.py").take(5).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}