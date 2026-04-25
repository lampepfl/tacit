requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val refs = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/.git/refs/heads")
  def listRefs(dir: FileEntry, prefix: String = ""): Unit = 
    dir.children.foreach { f => 
      if f.isDirectory then listRefs(f, prefix + f.name + "/") 
      else println(prefix + f.name)
    }
  listRefs(refs)
}