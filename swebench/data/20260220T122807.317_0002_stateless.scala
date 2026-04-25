requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val adminUtils = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django/contrib/admin/utils.py")
  val lines = adminUtils.readLines()
  
  // Print lines 380 to 420 (the display_for_field function)
  for i <- 380 to 420 do
    println(s"${i+1}: ${lines(i)}")
}
