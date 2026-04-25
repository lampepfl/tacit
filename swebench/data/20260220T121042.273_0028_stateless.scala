requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python")) {
    // RFC850 format uses dashes and 2-digit year
    val result = exec("python", List("-c", 
      "import django.utils.http as h; " +
      "print('Test RFC850 year 80 (should be 1980):', h.parse_http_date('Thursday, 01-Jan-80 00:00:00 GMT')); " +
      "print('Test RFC850 year 25 (should be 2025):', h.parse_http_date('Thursday, 01-Jan-25 00:00:00 GMT')); " +
      "print('Test RFC850 year 76 (should be 1976):', h.parse_http_date('Thursday, 01-Jan-76 00:00:00 GMT')); " +
      "print('Test RFC850 year 26 (should be 2026):', h.parse_http_date('Thursday, 01-Jan-26 00:00:00 GMT'))"
    ))
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exit: ${result.exitCode}")
  }
}