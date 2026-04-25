
requestFileSystem(".") {
    // Let's see what's in the current directory
    println("Current directory contents:")
    access(".").children.foreach(f => println(s"  ${f.name} (isClassified=${f.isClassified})"))
    
    // Check if secrets exists
    val secretsDir = access("secrets")
    println(s"\nsecrets exists: ${secretsDir.exists}, isDirectory: ${secretsDir.isDirectory}, isClassified: ${secretsDir.isClassified}")
    
    // List contents of secrets
    if secretsDir.exists then
        println("secrets contents:")
        secretsDir.children.foreach(f => println(s"  ${f.name} (isClassified=${f.isClassified})"))
}
