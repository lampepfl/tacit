requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", 
      "import datetime; import django.utils.http as h; " +
      "# Test with a 2-digit year that would be more than 50 years in future " +
      "# Current year is around 2026, so 80 would be 2080 which is > 50 years from 2026, " +
      "# so it should become 1980. " +
      "# 25 would be 2025 which is < 50 years in future, so it stays 2025. " +
      "print('Testing year 80 (should become 1980):', h.parse_http_date('Thu, 01 Jan 80 00:00:00 GMT'));" +
      "print('Testing year 25 (should become 2025):', h.parse_http_date('Thu, 01 Jan 25 00:00:00 GMT'));" +
      "print('Testing year 76 (should become 2076 > 2026+50=2076, so 1976):', h.parse_http_date('Thu, 01 Jan 76 00:00:00 GMT'))"
    ))
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exit: ${result.exitCode}")
  }
}