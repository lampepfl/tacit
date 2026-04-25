requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  // Try to delete test file if it exists
  try
    val testScript = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/test_inner_class.py")
    if testScript.exists then testScript.delete()
  catch
    case e: Exception => println("Could not delete test file")
  
  println("Cleanup done")
}