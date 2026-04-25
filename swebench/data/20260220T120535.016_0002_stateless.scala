requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo") { 
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo")
  val django = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11797/repo/django")
  django.children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}