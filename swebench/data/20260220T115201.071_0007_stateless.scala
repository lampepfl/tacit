import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11049/repo") { (fs: FileSystem) ?=>
  given FileSystem = fs
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11049/repo/django/db/models/fields/__init__.py")
  val content = f.read()
  println(s"Contains old: ${content.contains("[DD] [HH:[MM:]]ss[.uuuuuu]")}")
  println(s"Contains new: ${content.contains("[DD] [[HH:]MM:]ss[.uuuuuu]")}")
}