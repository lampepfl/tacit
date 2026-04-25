requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/tests/test_ndarithmetic.py")
  val lines = f.readLines()
  // Print lines around 1195-1230
  for (i <- 1195 to 1235) {
    println(s"${i+1}: ${lines(i)}")
  }
}