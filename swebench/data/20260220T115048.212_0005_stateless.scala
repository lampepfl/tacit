import library.*
import language.experimental.captureChecking

@main def test() = 
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11039/repo") { (fs: FileSystem ^) =>
    val files = find("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11039/repo", "*sqlmigrate*")
    files
  }