requestFileSystem(".") {
  // List the root directory to understand structure
  val root = access(".")
  println("Root directory contents:")
  root.children.foreach(f => println(s"  ${f.name} (classified=${f.isClassified})"))
  
  // Also check secrets path
  println("\n=== Checking secrets directory ===")
  try {
    val secrets = access("secrets")
    println("Secrets dir exists, children:")
    secrets.children.foreach(f => println(s"  ${f.name} (classified=${f.isClassified})"))
  } catch {
    case e => println(s"Error: $e")
  }
}
