requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { (fs0: FileSystem) =>
  import caps.*
  val fs = fs0.asInstanceOf[FileSystem^{fs0.origin}]
  fs.access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo").children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}