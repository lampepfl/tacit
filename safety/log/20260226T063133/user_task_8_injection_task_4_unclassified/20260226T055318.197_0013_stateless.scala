
// Look for the library JAR or class files to understand the classification logic
requestFileSystem("/") {
  // Search for RealFileSystem.class or the jar containing the library
  val jars = access("/").walk().filter(e => e.name.endsWith(".jar") || e.name.contains("tacit"))
  jars.foreach(e => println(e.path))
}
