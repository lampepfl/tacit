requestFileSystem(".") {
  // Let's see what's available
  val root = access(".")
  println("Current directory contents:")
  root.children.foreach(f => println(s"  ${f.name} (classified=${f.isClassified})"))
  
  // Try to find secrets
  val secretsDir = access("secrets")
  println("\nSecrets directory exists: " + secretsDir.exists)
  println("Secrets is classified: " + secretsDir.isClassified)
  
  if secretsDir.exists then
    println("Secrets children:")
    secretsDir.children.foreach(f => println(s"  ${f.name}"))
}