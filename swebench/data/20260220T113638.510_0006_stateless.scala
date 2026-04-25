requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-12907/repo") { 
  val f = access("astropy/modeling/tests/test_separable.py")
  val lines = f.readLines()
  // Look for tests related to nested compound models 
  lines.zipWithIndex.filter(_._1.contains("Nested") || _._1.contains("nested")).foreach(println)
}