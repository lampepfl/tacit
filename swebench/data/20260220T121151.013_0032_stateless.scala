requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  requestExecPermission(Set("python")) {
    // Verify dates from timestamps
    val result = exec("python", List("-c", 
      "import datetime; " +
      "print('3313526400:', datetime.datetime.utcfromtimestamp(3313526400)); " +
      "print('3281990400:', datetime.datetime.utcfromtimestamp(3281990400)); " +
      "print('315532800:', datetime.datetime.utcfromtimestamp(315532800)); " +
      "print('3345062400:', datetime.datetime.utcfromtimestamp(3345062400))"
    ))
    println(s"stdout: ${result.stdout}")
  }
}