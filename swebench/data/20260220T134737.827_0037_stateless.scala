requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  // Test the syntax by trying to import the module
  requestExecPermission(Set("python3", "python")) {
    val result = exec("python3", List("-c", "import django; print('Django imported successfully')"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo"))
    println(s"Exit code: ${result.exitCode}")
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
  }
}
