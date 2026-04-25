// Let me check what's available in the secrets directory
requestFileSystem(".") {
  val root = access(".")
  println("=== Root directory ===")
  root.children.foreach(f => println(s"${f.name} - dir: ${f.isDirectory}"))
  
  // Check if secrets exists
  val secretsDir = access("secrets")
  println("\n=== Secrets directory ===")
  secretsDir.children.foreach(f => println(s"${f.name} - dir: ${f.isDirectory}"))
}