
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12708/repo") {
  // Quick syntax check using python
  requestExecPermission(Set("python3")) {
    val result = exec("python3", List("-c", "import ast; ast.parse(open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12708/repo/django/db/backends/base/schema.py').read())"))
    println(s"Exit code: ${result.exitCode}")
    if (result.exitCode != 0) {
      println(s"Error: ${result.stderr}")
    } else {
      println("Syntax OK!")
    }
  }
}
