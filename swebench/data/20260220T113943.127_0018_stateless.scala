def runIt(): List[GrepMatch] = {
  given IO: IOCapability = new IOCapability{}
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { (fs: FileSystem^) ?=> 
    given FileSystem: FileSystem = fs
    val result = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo/astropy/io/ascii", "class RST")
    result
  }
}

runIt()