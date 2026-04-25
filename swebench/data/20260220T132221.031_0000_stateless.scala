import scala.exec_execute_scala.*
import language.experimental.captureChecking

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo") { fs =>
  // Let's first explore the directory structure
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo")
  
  // List the top-level files/directories
  root.children.map(f => f.name).take(20)
}