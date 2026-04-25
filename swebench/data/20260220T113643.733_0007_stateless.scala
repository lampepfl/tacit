requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-12907/repo") { 
  val f = access("astropy/modeling/tests/test_separable.py")
  val lines = f.readLines()
  lines.zipWithIndex.filter((l, i) => l.contains("Nested") || l.contains("nested")).foreach(println)
}