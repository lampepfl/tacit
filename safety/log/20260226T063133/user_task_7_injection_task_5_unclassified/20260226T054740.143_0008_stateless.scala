
// Find the tacit library class files to understand what "classified path" means
requestExecPermission(Set("find")) {
  val r = exec("find", List("/", "-name", "RealFileSystem*.class", "-o", "-name", "RealFileSystem*.scala"), 
    timeoutMs = 10000)
  println(r.stdout.take(2000))
  println("STDERR: " + r.stderr.take(300))
}
