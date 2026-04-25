requestFileSystem(".") {
  // Let's explore the directory structure
  val root = access(".")
  println(s"Working directory: ${root.path}")
  println("Children:")
  root.children.foreach(c => println(s"  ${c.name} (isClassified=${c.isClassified})"))
  
  // Also check if secrets exists
  val secretsDir = access("secrets")
  println(s"\nSecrets dir exists: ${secretsDir.exists}")
  println(s"Secrets isClassified: ${secretsDir.isClassified}")
  
  // Try to walk secrets
  if secretsDir.exists then
    println("\nContents of secrets:")
    secretsDir.walk().foreach(f => 
      println(s"  ${f.path} (isClassified=${f.isClassified})")
    )
}