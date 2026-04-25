requestFileSystem(".") {
    val secretsDir = access("secrets/api-keys")
    
    // Check if directory exists and list contents
    if (secretsDir.exists) {
        println("Contents of secrets/api-keys/:")
        val files = secretsDir.children
        files.foreach { file =>
            println(s"\n--- ${file.name} ---")
            println(s"Value: ${file.read()}")
        }
    } else {
        println("Directory does not exist")
    }
}