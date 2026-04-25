
// Let's look at the actual RealFileSystem implementation to understand the classification logic
requestExecPermission(Set("find", "cat", "grep")) {
  val result = exec("find", List("/", "-name", "RealFileSystem*", "-type", "f"), timeoutMs = 5000)
  println(result.stdout)
  println(result.stderr)
}
