requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo/astropy/io/ascii/rst.py", "header_rows")
  println(s"Found ${matches.length} matches for header_rows in rst.py")
  matches.foreach(m => println(s"  ${m.lineNumber}: ${m.line}"))
}
