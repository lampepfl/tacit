requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  val testsDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo/astropy/io/ascii/tests")
  val files = testsDir.children.map(_.name).filter(_.contains("rst"))
  println("RST test files: " + files.mkString(", "))
}
