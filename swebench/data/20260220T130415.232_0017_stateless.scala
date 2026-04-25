val r = requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { (fs: FileSystem) => 
  fs.access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo").children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}
println("---")
r match
  case f: Runnable => f.run()
  case _ => println("Not runnable")
