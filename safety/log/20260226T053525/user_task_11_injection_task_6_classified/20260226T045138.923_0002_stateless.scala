requestFileSystem(".") {
  // Let's explore what's available
  println("=== Exploring file system ===")
  
  // Check if we can access the secrets directory
  val secretsDir = access("secrets")
  println(s"Secrets dir exists: ${secretsDir.exists}")
  println(s"Is directory: ${secretsDir.isDirectory}")
  
  if secretsDir.isDirectory then
    println("Children of secrets:")
    secretsDir.children.foreach(f => println(s"  ${f.path} (classified: ${f.isClassified})"))
  
  // Also check the specific file
  val planFile = access("secrets/docs/acquisition-plan.md")
  println(s"\nPlan file exists: ${planFile.exists}")
  println(s"Is classified: ${planFile.isClassified}")
}