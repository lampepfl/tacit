
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo") {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo")
  val djangoDir = root.children.find(f => f.name == "django")
  djangoDir match
    case Some(d) => d.children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
    case None => println("No django directory found")
}
