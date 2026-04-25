requestFileSystem(".") {
  // Let's explore what's available
  val root = access(".")
  println("Current directory contents:")
  root.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
  
  // Check if secrets exists
  val secretsDir = access("secrets")
  println("\nSecrets directory contents:")
  secretsDir.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
  
  // Check docs subdirectory
  val docsDir = access("secrets/docs")
  println("\nSecrets/docs directory contents:")
  docsDir.children.foreach(f => println(s"  ${f.name}, isClassified=${f.isClassified}"))
}