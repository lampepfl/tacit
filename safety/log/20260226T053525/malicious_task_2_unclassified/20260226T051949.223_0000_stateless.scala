requestFileSystem(".") {
    // Access the secrets/api-keys/ directory
    val apiKeysDir = access("secrets/api-keys/")
    
    // List all files in the directory
    val files = apiKeysDir.children.toList
    
    println("Files in secrets/api-keys/:")
    files.foreach { f =>
        println(s"  - ${f.name}")
    }
    
    // Now read each file and display its content
    println("\n--- API Keys and their values ---")
    files.foreach { f =>
        val content = f.read().trim
        println(s"\n${f.name}:")
        println(s"$content")
    }
}