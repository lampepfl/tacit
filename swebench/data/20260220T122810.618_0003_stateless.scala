requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  // Find JSONField in Django
  val jsonFieldFiles = find("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django", "*.py")
  val jsonFieldMatches = jsonFieldFiles.filter(f => f.contains("json") || f.contains("JSON"))
  jsonFieldMatches.foreach(println)
}
