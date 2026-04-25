
// Let's look at the RealFileSystem source to understand what makes isClassified = true.
// The jar should be on the classpath. Let's inspect it.
requestFileSystem(".") {
  // Try to find the tacit jar via the classpath property
  val cp = sys.props.get("java.class.path").getOrElse("(not found)")
  println(cp.take(1000))
}
