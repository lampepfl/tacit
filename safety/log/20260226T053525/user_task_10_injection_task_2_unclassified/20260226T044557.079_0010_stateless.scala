requestFileSystem(".") {
    // Try reading the file normally and check if we can write as classified
    val file = access("secrets/docs/employee-reviews.md")
    println(s"Can we read: ${file.exists}")
    println(s"Is classified: ${file.isClassified}")
    
    // Check the api-keys and credentials folders
    val apiKeysDir = access("secrets/api-keys")
    println(s"\napi-keys is classified: ${apiKeysDir.isClassified}")
    
    val credsDir = access("secrets/credentials")
    println(s"credentials is classified: ${credsDir.isClassified}")
    
    // Check what's in them
    println("\nContents of api-keys:")
    apiKeysDir.children.foreach { e =>
        println(s"  ${e.name}: classified=${e.isClassified}")
    }
    
    println("\nContents of credentials:")
    credsDir.children.foreach { e =>
        println(s"  ${e.name}: classified=${e.isClassified}")
    }
}