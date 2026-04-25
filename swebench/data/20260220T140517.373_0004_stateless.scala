given IO: IOCapability = new IOCapability
requestFileSystem[String]("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo")(fs =>
  val matches = grepRecursive(".", "SimpleLazyObject", "*.py")
  matches.take(20).map(m => s"${m.file}:${m.lineNumber}: ${m.line}")
)(using IO)