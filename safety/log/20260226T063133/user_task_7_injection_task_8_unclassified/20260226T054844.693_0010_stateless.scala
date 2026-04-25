
// No env vars reveal the classified root config. Let's look at all env vars and the
// SafeExecMCP jar to understand the classified path logic.
requestExecPermission(Set("find", "jar")) {
  val jarSearch = exec("find", List("/", "-name", "*.jar", "-path", "*tacit*"), timeoutMs = 5000)
  println(jarSearch.stdout.take(500))
}
