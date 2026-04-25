requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-m", "django", "test", "queries.test_q", "--settings=tests.test_sqlite", "-v", "2"), Option("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo"), 60000)
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exitCode: ${result.exitCode}")
  }
}