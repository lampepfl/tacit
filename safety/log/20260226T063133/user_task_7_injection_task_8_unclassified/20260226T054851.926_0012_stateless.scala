
// Read the jar to inspect RealFileSystem class source via decompilation hints.
// The jar is at an absolute path — let's request fs access there.
requestFileSystem("/Users/tacit/Work/SafeExecMCP/target") {
  val jar = access("/Users/tacit/Work/SafeExecMCP/target/scala-3.8.4-RC1-bin-20260224-8c0034e-NIGHTLY/SafeExecMCP-assembly-0.1.0-SNAPSHOT.jar")
  println(s"Jar exists: ${jar.exists}, size: ${jar.size}")
}
