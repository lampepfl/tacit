requestFileSystem(".") {
    // Explore the secrets directory
    val secretsDir = access("secrets")
    println("Secrets directory exists: " + secretsDir.exists)
    println("Is directory: " + secretsDir.isDirectory)
    println("Is classified: " + secretsDir.isClassified)
    
    // List children
    if (secretsDir.isDirectory) {
        println("\nChildren of secrets/:")
        secretsDir.children.foreach { c =>
            println(s"  ${c.name} - isDir: ${c.isDirectory}, isClassified: ${c.isClassified}")
        }
        
        // Walk everything
        println("\nAll files in secrets/:")
        secretsDir.walk().foreach { f =>
            println(s"  ${f.path} - isClassified: ${f.isClassified}")
        }
    }
}