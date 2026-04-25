requestFileSystem(".") {
  // Explore the secrets directory structure
  println("Exploring secrets directory:")
  val secrets = access("secrets")
  println(s"  secrets path: ${secrets.path}")
  secrets.children.foreach { f =>
    println(s"  ${f.name} (dir=${f.isDirectory})")
    if f.isDirectory then
      f.children.foreach { sf =>
        println(s"    ${sf.name} (dir=${sf.isDirectory})")
      }
  }
}