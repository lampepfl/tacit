requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/django/db/models/fields/__init__.py")
  val content = f.read()
  val start = content.indexOf("def contribute_to_class")
  val lines = content.substring(start).split("\n")
  (0 to 40).foreach(i => println(s"$i: ${lines(i)}"))
}