requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val adminUtils = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django/contrib/admin/utils.py")
  val lines = adminUtils.readLines()
  
  // Print first 20 lines to see imports
  for i <- 0 to 20 do
    println(s"${i+1}: ${lines(i)}")
}
