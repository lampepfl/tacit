import scala.language.experimental.captureChecking

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo") { fs =>
  given FileSystem = fs
  val root = access(".")
  root.children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}