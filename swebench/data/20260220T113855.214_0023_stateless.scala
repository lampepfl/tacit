requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val lines = f.readLines()
  // Get lines 519-523 and show their repr to see exact whitespace
  println("Line 519 (idx 518):")
  println(lines(518).toSeq.mkString)
  println("Line 520 (idx 519):")
  println(lines(519).toSeq.mkString)
  println("Line 521 (idx 520):")
  println(lines(520).toSeq.mkString)
  println("Line 522 (idx 521):")
  println(lines(521).toSeq.mkString)
  println("Line 523 (idx 522):")
  println(lines(522).toSeq.mkString)
}
