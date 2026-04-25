
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14365/repo") {
  given fs: FileSystem = summon
  val content = access("astropy/io/ascii/qdp.py").read()
  println(content)
}
