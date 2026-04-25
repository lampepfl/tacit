requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { (_: FileSystem) =>
  access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo").children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}