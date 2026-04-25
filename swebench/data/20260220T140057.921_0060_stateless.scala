requestExecPermission(Set("git")) { 
  val r = exec("git", List("diff", "--no-color"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo"))
  println(r.stdout)
}