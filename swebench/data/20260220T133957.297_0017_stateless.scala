requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo") { (fs: FileSystem) =>
  given FileSystem = fs
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py", "class ResolverMatch")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}