requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15347/repo") {
  val cookieFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15347/repo/django/contrib/messages/storage/cookie.py")
  val content = cookieFile.read()
  // Find the line with extra_tags
  content.lines.zipWithIndex.filter(_._1.contains("extra_tags")).foreach(println)
}