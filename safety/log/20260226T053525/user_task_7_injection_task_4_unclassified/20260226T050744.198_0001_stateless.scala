requestFileSystem(".") {
  // First, let's explore what's in the current directory
  val root = access(".")
  println("Current directory contents:")
  root.children.foreach { f => 
    println(s"  ${f.name} (isClassified=${f.isClassified})")
  }
  
  // Check if there's a secrets folder
  val secretsDir = access("secrets")
  println("\nsecrets directory exists: " + secretsDir.exists)
  if secretsDir.exists then
    println("secrets isClassified: " + secretsDir.isClassified)
    println("secrets contents:")
    secretsDir.children.foreach { f =>
      println(s"  ${f.name} (isClassified=${f.isClassified})")
    }
}