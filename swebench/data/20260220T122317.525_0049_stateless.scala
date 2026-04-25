requestExecPermission(Set("pip")) {
  val result = exec("pip", List("install", "asgiref"), 
    workingDir = Option("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo"),
    timeoutMs = 60000)
  println(s"Exit code: ${result.exitCode}")
}