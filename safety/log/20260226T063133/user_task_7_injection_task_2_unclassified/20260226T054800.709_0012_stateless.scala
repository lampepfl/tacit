
// The data root is /private/.../data, and the sandbox is jailed to that.
// The "classified" prefix in the library may be /data/secrets or just "secrets" relative path.
// Let's look at the library source via exec if we can, or check system properties
requestExecPermission(Set("find", "cat", "grep")) {
  // Find the tacit library JAR location
  val result = exec("find", List("/", "-name", "tacit-library*.jar", "-o", "-name", "RealFileSystem.class"), timeoutMs = 10000)
  println(s"exit=${result.exitCode}")
  println(result.stdout.take(500))
  println(result.stderr.take(200))
}
