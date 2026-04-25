requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14411/repo") { 
  val f = access("django/contrib/auth/forms.py")
  val content = f.read()
  val hasReadOnly = content.contains("ReadOnlyPasswordHash")
  println(s"Contains ReadOnlyPasswordHash: $hasReadOnly")
}
