requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python")) {
    // Test year 77 which should become 1977 (> 50 years)
    val result = exec("python", List("-c", 
      "import django.utils.http as h; " +
      "print('Test year 77 (should be 1977 > 50 years):', h.parse_http_date('Thursday, 01-Jan-77 00:00:00 GMT'))"
    ))
    println(s"stdout: ${result.stdout}")
  }
}