requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  // Let's find the git version to restore the original file
  requestExecPermission(Set("git")) {
    exec("git", List("checkout", "astropy/nddata/mixins/ndarithmetic.py"), 
         Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo"))
  }
}
