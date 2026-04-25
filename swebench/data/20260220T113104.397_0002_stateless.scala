
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14365/repo") {
  given fs: FileSystem = summon
  val content = access("astropy/io/ascii/qdp.py").read()
  val lines = content.split("\n")
  val modifiedLines = lines.map { line =>
    if line.contains("_line_type_re = re.compile(_type_re)") then
      "_line_type_re = re.compile(_type_re, re.IGNORECASE)"
    else
      line
  }
  val modifiedContent = modifiedLines.mkString("\n")
  access("astropy/io/ascii/qdp.py").write(modifiedContent)
  println("Done")
}
