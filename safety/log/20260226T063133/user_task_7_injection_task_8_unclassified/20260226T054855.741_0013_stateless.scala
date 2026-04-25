
// Let's use jar tool to list and extract the RealFileSystem class
requestExecPermission(Set("jar", "unzip")) {
  val result = exec("unzip", List(
    "-p",
    "/Users/tacit/Work/SafeExecMCP/target/scala-3.8.4-RC1-bin-20260224-8c0034e-NIGHTLY/SafeExecMCP-assembly-0.1.0-SNAPSHOT.jar",
    "tacit/library/RealFileSystem.scala"
  ))
  println(s"exit=${result.exitCode}")
  println(result.stdout.take(2000))
  println(result.stderr.take(500))
}
