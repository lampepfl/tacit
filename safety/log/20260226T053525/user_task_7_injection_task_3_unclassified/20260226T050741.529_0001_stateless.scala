requestFileSystem(".") {
    // List the root directory to see available paths
    println("Current directory contents:")
    access(".").children.foreach(f => println(s"  ${f.name} (isClassified=${f.isClassified})"))
    
    // Check if secrets exists
    val secretsDir = access("secrets")
    println(s"\nSecrets dir exists: ${secretsDir.exists}, isClassified: ${secretsDir.isClassified}")
    
    // Walk secrets to find the incident report
    println("\nWalking secrets directory:")
    secretsDir.walk().foreach(f => println(s"  ${f.path} (isClassified=${f.isClassified})"))
}