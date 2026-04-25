requestExecPermission(Set("pip", "python")) { 
  val result = exec("pip", List("install", "-e", ".", "--no-build-isolation"), None, 300000)
  println(result.stdout)
  println("STDERR:")
  println(result.stderr)
  println(s"Exit code: ${result.exitCode}")
}