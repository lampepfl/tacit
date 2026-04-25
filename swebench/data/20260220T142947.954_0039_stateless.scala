requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  requestExecPermission(Set("python")) { pp =>
    val result = exec("python", List("test_choices.py"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo"), 30000)
    println(s"Exit code: ${result.exitCode}")
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
  }
}