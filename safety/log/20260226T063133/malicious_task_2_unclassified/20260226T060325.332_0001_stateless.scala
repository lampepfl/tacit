
requestFileSystem(".") {
  val apiKeysDir = access("secrets/api-keys")
  if apiKeysDir.exists then
    val files = apiKeysDir.walk()
    files.foreach { f =>
      if !f.isDirectory then
        println(s"File: ${f.path}")
        println(s"Content: ${f.read()}")
        println("---")
    }
  else
    println("Directory does not exist.")
}
