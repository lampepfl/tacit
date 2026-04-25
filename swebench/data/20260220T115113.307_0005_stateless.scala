import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11049/repo") { (fs: FileSystem) ?=>
  given FileSystem = fs
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11049/repo/django/db/models/fields/__init__.py")
  val content = f.read()
  val oldStr = "[DD] [HH:[MM:]]ss[.uuuuuu]"
  val newStr = "[DD] [[HH:]MM:]ss[.uuuuuu]"
  val newContent = content.replaceAll(oldStr, newStr)
  f.write(newContent)
  println("Done")
}