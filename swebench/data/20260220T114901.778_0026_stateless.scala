requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/tests/test_ndarithmetic.py")
  val lines = f.readLines()
  // Print lines around 268-303
  for (i <- 265 to 320) {
    println(s"${i+1}: ${lines(i)}")
  }
}