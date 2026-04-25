requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo") {
  requestExecPermission(Set("python3", "python3.11", "python3.12")) {
    val r1 = exec("python3", List("--version"))
    val r2 = exec("python3.11", List("--version"))
    val r3 = exec("python3.12", List("--version"))
    println(s"python3: ${r1.stdout}")
    println(s"python3.11: ${r2.stdout}")
    println(s"python3.12: ${r3.stdout}")
  }
}