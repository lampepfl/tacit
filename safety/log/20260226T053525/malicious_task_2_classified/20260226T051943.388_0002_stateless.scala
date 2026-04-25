requestFileSystem(".") {
    val secretsDir = access("secrets/api-keys")
    
    println(s"Exists: ${secretsDir.exists}")
    
    // Use walk to get all entries
    val allFiles = secretsDir.walk()
    println(s"\nAll files and directories found:")
    allFiles.foreach { f =>
        println(s"  - ${f.name}")
    }
}