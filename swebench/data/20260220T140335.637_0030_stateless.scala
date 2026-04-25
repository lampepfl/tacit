def listDir(fs: FileSystem): String = {
  given FileSystem = fs
  println("EXECUTING LISTDIR")
  val entries = access(".").children
  entries.map(_.name).mkString(", ")
}

val result = requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo")(listDir)
println("Result type: " + result.getClass.getName)
println("Result: " + result)