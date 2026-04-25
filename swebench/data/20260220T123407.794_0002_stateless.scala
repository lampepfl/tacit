
import scala.collection.mutable.ListBuffer

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo") { (fs: FileSystem) =>
  val root = fs.access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo")
  val children = root.children
  val buffer = ListBuffer[String]()
  children.take(20).foreach(f => buffer.append(s"${f.name} (dir=${f.isDirectory})"))
  buffer.mkString("\n")
}
