requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", 
      "import datetime; print('220924800:', datetime.datetime.utcfromtimestamp(220924800))"
    ))
    println(s"stdout: ${result.stdout}")
  }
}