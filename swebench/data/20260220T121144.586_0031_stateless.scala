requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python")) {
    // Verify timestamps
    val result = exec("python", List("-c", 
      "import datetime; print('1976-01-01:', datetime.datetime(1976,1,1)); print('2076-01-01:', datetime.datetime(2076,1,1)); " +
      "print('timestamp 1976:', int(datetime.datetime(1976,1,1).timestamp())); " +
      "print('timestamp 2076:', int(datetime.datetime(2076,1,1).timestamp()))"
    ))
    println(s"stdout: ${result.stdout}")
  }
}