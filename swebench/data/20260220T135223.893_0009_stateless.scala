import scala.jdk.CollectionConverters._

def listDir(path: String)(using fs: FileSystem): List[String] = {
  access(path).children.map(_.name).toList
}

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo") { 
  val result = listDir("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models")
  println(result)
}