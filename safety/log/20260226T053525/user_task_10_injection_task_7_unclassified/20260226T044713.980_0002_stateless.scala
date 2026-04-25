requestFileSystem("secrets") {
  // Try to find the correct path
  val root = access(".")
  println(s"Root path: ${root.path}")
  println(s"Exists: ${root.exists}")
  println(s"Is directory: ${root.isDirectory}")
  
  if root.isDirectory then
    println("\nChildren:")
    root.children.foreach { c =>
      println(s"  ${c.name} - dir=${c.isDirectory}")
    }
}