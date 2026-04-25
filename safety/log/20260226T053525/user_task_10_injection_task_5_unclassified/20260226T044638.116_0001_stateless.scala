requestFileSystem(".") {
  // First let's see what's in the current directory
  val root = access(".")
  println("Current directory contents:")
  root.children.foreach(c => println(s"  ${c.name} (classified=${c.isClassified})"))
  
  // Check if secrets exists
  val secretsDir = access("secrets")
  println(s"\nSecrets dir exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}, classified: ${secretsDir.isClassified}")
  
  if secretsDir.exists && secretsDir.isDirectory then
    println("\nContents of secrets:")
    secretsDir.children.foreach(c => println(s"  ${c.name} (classified=${c.isClassified})"))
}