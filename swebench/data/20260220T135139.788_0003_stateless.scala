given fs: FileSystem = null.asInstanceOf[FileSystem]
val path = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/expressions.py"
val f = access(path)
println(f"exists=${f.exists}, name=${f.name}")