requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo") { 
  val django = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo/django")
  django.children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}
