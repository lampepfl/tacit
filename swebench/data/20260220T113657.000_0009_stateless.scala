requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-12907/repo") { 
  val f = access("astropy/modeling/separable.py")
  val lines = f.readLines()
  lines.slice(240, 250).zipWithIndex.foreach { case (line, i) => println(s"${i+241}: $line") }
}