def myOp(using io: IOCapability) = {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo")
  val children = root.children.map(_.name).take(30)
  println(s"Children: ${children.mkString(", ")}")
}
myOp