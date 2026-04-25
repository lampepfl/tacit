import library.*
import language.experimental.captureChecking

def test()(using fs: FileSystem): Unit = {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14915/repo")
  root.children.take(20).foreach(c => println(c.name))
}

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14915/repo") { test() }