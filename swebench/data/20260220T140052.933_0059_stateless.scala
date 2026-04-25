def runGit(args: List[String]) = 
  requestExecPermission(Set("git")) { pp =>
    exec("git", args, workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo"))
  }

runGit(List("diff", "--no-color"))