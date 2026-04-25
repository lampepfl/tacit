requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", 
      """import django.utils.http as h
print('Test 1 - year 80:', h.parse_http_date('Thu, 01 Jan 80 00:00:00 GMT'))
print('Test 2 - year 25:', h.parse_http_date('Thu, 01 Jan 25 00:00:00 GMT'))
print('Test 3 - year 76:', h.parse_http_date('Thu, 01 Jan 76 00:00:00 GMT'))
print('Current year test - year 26:', h.parse_http_date('Thu, 01 Jan 26 00:00:00 GMT'))
"""
    ))
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exit: ${result.exitCode}")
  }
}