requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-6938/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-6938/repo/astropy/io/fits/fitsrec.py")
  val lines = f.readLines()
  // Print lines 1255-1275 for context
  (1254 to 1274).foreach { idx =>
    println(s"${idx + 1}: ${lines(idx)}")
  }
}
