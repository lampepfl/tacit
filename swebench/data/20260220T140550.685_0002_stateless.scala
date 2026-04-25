import scala.exec.{requestFileSystem, access, grep, grepRecursive, find, exec, requestExecPermission, ProcessResult, IOCapability, Interface}

val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15738/repo")
root.children.foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))