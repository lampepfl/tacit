requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  requestExecPermission(Set("python", "python3")) {
    // Try to run the tests
    val result = exec("python", List("./tests/runtests.py", "model_fields.test_manytomanyfield", "-v", "2"), 
        workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo"),
        timeoutMs = 120000)
    println(s"Exit code: ${result.exitCode}")
    println(s"stdout: ${result.stdout.take(3000)}")
    println(s"stderr: ${result.stderr.take(1000)}")
  }
}
