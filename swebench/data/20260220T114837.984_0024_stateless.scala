requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/tests/test_ndarithmetic.py")
  // Search for mask-related tests
  val content = f.read()
  val matches = content.lines.zipWithIndex.filter { case (line, idx) => 
    line.toLowerCase.contains("mask") && line.toLowerCase.contains("test")
  }
  println("Mask-related test methods:")
  matches.foreach { case (line, idx) =>
    println(s"${idx+1}: $line")
  }
}