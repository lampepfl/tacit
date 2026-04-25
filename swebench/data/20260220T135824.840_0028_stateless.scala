def myOp(using io: IOCapability) = {
  val step1 = requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo")
  step1.applyOrElse(
    (fs: library.FileSystem) => {
      val root = fs.access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo")
      println(s"Root: ${root.name}")
    },
    (x: Any) => println(s"else: $x")
  )(using io)
}
myOp