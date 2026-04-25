requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-6938/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-6938/repo/astropy/io/fits/fitsrec.py")
  val content = f.read()
  val lines = content.split("\n")
  lines.zipWithIndex.foreach { case (line, idx) =>
    if line.contains("Replace exponent separator") || line.contains("encode_ascii('E')") || line.contains("encode_ascii('D')") then
      println(s"${idx + 1}: $line")
  }
}
