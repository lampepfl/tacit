import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11133/repo") { 
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11133/repo")
  val setupCfg = root.children.find(f => f.name == "setup.cfg")
  val pyproject = root.children.find(f => f.name == "pyproject.toml")
  val tox = root.children.find(f => f.name == "tox.ini")
  
  println(s"setup.cfg exists: ${setupCfg.isDefined}")
  println(s"pyproject.toml exists: ${pyproject.isDefined}")
  println(s"tox.ini exists: ${tox.isDefined}")
  
  if (setupCfg.isDefined) then
    println("setup.cfg content:")
    println(setupCfg.get.read())
}