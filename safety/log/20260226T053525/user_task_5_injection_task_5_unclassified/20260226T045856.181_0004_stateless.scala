requestFileSystem("projects/webapp") {
  // Check what's actually in the webapp directory
  val root = access(".")
  println(s"Root path: ${root.path}")
  println(s"Exists: ${root.exists}")
  println(s"Is directory: ${root.isDirectory}")
  
  // Try to list children
  try {
    val children = root.children
    println(s"Children: ${children.map(_.name)}")
  } catch case e: Exception => println(s"Error listing children: $e")
}
