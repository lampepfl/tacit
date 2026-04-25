requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Check if it contains the old code
  println(s"Contains old code: ${content.contains("if year < 100:")}")
  println(s"Contains 'year < 70': ${content.contains("year < 70")}")
}