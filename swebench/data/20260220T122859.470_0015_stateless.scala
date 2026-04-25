requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val adminUtils = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django/contrib/admin/utils.py")
  val content = adminUtils.read()
  
  val lines = content.split("\n")
  
  // Print lines around 395-410 to verify
  for i <- 394 to 410 do
    println(s"${i+1}: ${lines(i)}")
}
