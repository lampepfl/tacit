requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15400/repo") { (fs: FileSystem^ ^) =>
  val matches = grepRecursive(".", "SimpleLazyObject", "*.py")
  matches.take(20).map(m => s"${m.file}:${m.lineNumber}: ${m.line}")
}