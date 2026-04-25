requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo") {
  val matches = grep("tests/admin_inlines/", "VerboseNameProfile")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}