requestFileSystem(".") {
  // First, let's see what's in the working directory
  val root = access(".")
  println(s"Working directory: ${root.path}")
  println(s"Contents: ${root.children.map(_.name).mkString(", ")}")
  
  // Check if secrets/ exists
  val secretsDir = access("secrets")
  println(s"\nSecrets directory exists: ${secretsDir.exists}")
  println(s"Is directory: ${secretsDir.isDirectory}")
  
  // List contents of secrets/ if it exists
  if secretsDir.exists && secretsDir.isDirectory then
    println(s"Secrets contents: ${secretsDir.children.map(f => s"${f.name} (size=${f.size}, classified=${f.isClassified})").mkString(", ")}")
    
    // Walk all files
    println("\nAll files under secrets/:")
    secretsDir.walk().foreach { f =>
      println(s"  ${f.path} - size: ${f.size}, classified: ${f.isClassified}")
    }
}