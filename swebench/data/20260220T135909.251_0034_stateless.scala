def myOp(using io: IOCapability) = {
  val result = requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo")((fs: library.FileSystem) => {
    val root = fs.access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo")
    println(s"Root: ${root.name}")
  })
  result.asInstanceOf[Function0[Unit]].apply()
}
myOp