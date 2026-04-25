// Inspect the RealFileSystem class to understand what makes a path "classified"
// Try to find the library jar or source on the classpath
val cl = classOf[tacit.library.FileSystem].getClassLoader
val urls = cl match {
  case ucl: java.net.URLClassLoader => ucl.getURLs.toList
  case _ => List.empty
}
println(s"ClassLoader type: ${cl.getClass.getName}")
println(s"URLs: $urls")

// Also try to find the source via resource
val res = cl.getResource("tacit/library/RealFileSystem.class")
println(s"RealFileSystem class resource: $res")
