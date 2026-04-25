
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo") {
  requestExecPermission(Set("grep")) {
    val result = exec("grep", List("-n", "AlterField.*UniqueConstraint\\|UniqueConstraint.*AlterField", "tests/"))
    println(result.stdout)
  }
}
