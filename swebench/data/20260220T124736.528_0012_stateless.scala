requestFileSystem[Unit]("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo") { (fs: FileSystem) ?=>
  // Let's grep for "class Collector"
  val results = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo/django/db/models", "class Collector")
  results.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}