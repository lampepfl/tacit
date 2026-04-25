
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/autodetector.py")
  val content = f.read()
  
  // Check exact content
  val idx = content.indexOf("order_wrt_unset")
  println(s"Found at: $idx")
  println(s"Context: " + content.substring(idx-100, idx+200))
}
