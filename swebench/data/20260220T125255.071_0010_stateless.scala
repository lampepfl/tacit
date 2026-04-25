import scala.language.implicitConversions
import scala.language.experimental.captureChecking

def listDir(rootPath: String)(using fs: FileSystem): String = 
  access(rootPath).children.map(f => s"${f.name}").mkString("\n")

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo")(fs => listDir("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo")(using fs))