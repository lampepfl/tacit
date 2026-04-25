requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") {
  given fs: FileSystem = summon[FileSystem]
  val matches = grep("./django/utils/autoreload.py", "argv")
  matches.foreach(m => println(s"${m.lineNumber}: ${m.line}"))
}