
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12700/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  // Let's see the exact bytes around the cleanse_setting method
  val content = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12700/repo/django/views/debug.py").read()
  
  val lines = content.split("\n")
  val startLine = lines.indexWhere(_.contains("def cleanse_setting"))
  
  // Print more lines to see exact content
  for (i <- startLine until Math.min(startLine + 18, lines.length)) {
    println(s"$i: |${lines(i)}|")
  }
}
