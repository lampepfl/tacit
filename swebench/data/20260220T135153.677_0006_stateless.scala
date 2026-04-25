def test(fs: FileSystem) = {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo/django/db/models/expressions.py")
  println(f"exists=${f.exists}, name=${f.name}")
}

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15213/repo")(fs => test(fs))