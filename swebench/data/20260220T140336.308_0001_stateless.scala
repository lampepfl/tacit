requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo") {
  given fs: FileSystem = summon[FileSystem]
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo/django", "class RenameIndex", "*.py")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}