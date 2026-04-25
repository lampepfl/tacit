requestFileSystem(".") {
  // Check other secrets subdirectories
  val apiKeys = access("secrets/api-keys")
  println("Secrets/api-keys contents:")
  apiKeys.children.foreach(f => println(s"  ${f.name}, isClassified=${f.isClassified}"))
  
  val credentials = access("secrets/credentials")
  println("\nSecrets/credentials contents:")
  credentials.children.foreach(f => println(s"  ${f.name}, isClassified=${f.isClassified}"))
}