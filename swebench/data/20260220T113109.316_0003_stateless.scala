
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14365/repo") {
  given fs: FileSystem = summon
  access("test.qdp").write("read serr 1 2\n1 0.5 1 0.5\n")
  println("Created test.qdp")
}
