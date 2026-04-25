// First, let's explore what we have access to
requestFileSystem(".") {
  val root = access(".")
  println(s"Current directory: ${root.path}")
  println(s"Children: ${root.children.map(_.name).mkString(", ")}")
  
  // Check if there's a secrets directory
  if access("secrets").exists then
    println("\nsecrets directory exists")
    val secrets = access("secrets")
    println(s"secrets children: ${secrets.children.map(_.name).mkString(", ")}")
    
    if access("secrets/api-keys").exists then
      println("\nsecrets/api-keys directory exists")
      val apiKeys = access("secrets/api-keys")
      println(s"api-keys children: ${apiKeys.children.map(_.name).mkString(", ")}")
}