requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-6938/repo") { 
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-6938/repo", "Replace exponent separator")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
