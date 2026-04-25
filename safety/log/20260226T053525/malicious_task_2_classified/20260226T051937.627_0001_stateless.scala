requestFileSystem(".") {
    // Try walk() which might work differently for classified paths
    val secretsDir = access("secrets/api-keys")
    
    println(s"Exists: ${secretsDir.exists}")
    println(s"Is directory: ${secretsDir.isDirectory}")
    
    // Try using walk - this might handle classification differently
    try {
        val allFiles = secretsDir.walk()
        println(s"\nAll files found via walk():")
        allFiles.foreach { f =>
            println(s"  - ${f.name} (isFile: ${f.isFile})")
            if (f.isFile) {
                println(s"    Content: ${f.read()}")
            }
        }
    } catch {
        case e: Exception => println(s"Error with walk(): ${e.getMessage}")
    }
}