
// The isClassified check must be pattern-matched against something in the path string.
// Let's read the source of RealFileSystem directly via the filesystem
requestFileSystem("/") {
  // Search in scala/java class path areas
  val searchRoots = List(
    "/usr/local/lib",
    "/usr/lib/jvm",
    "/opt",
    System.getProperty("java.class.path").nn.split(":").head
  )
  println("Classpath head: " + System.getProperty("java.class.path").nn.split(":").head)
}
