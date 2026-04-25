requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python")) {
    // Test edge cases
    val result = exec("python", List("-c", 
      "import django.utils.http as h; " +
      "print('Test year 75 (2076, exactly 50 years, stays 2076):', h.parse_http_date('Thursday, 01-Jan-75 00:00:00 GMT')); " +
      "print('Test year 74 (2074, < 50 years, stays 2074):', h.parse_http_date('Thursday, 01-Jan-74 00:00:00 GMT'))"
    ))
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exit: ${result.exitCode}")
  }
}