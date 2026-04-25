requestFileSystem(".") {
    // Let's explore the directory structure
    println("Current directory contents:")
    access(".").children.foreach { f => 
        println(s"  ${f.name} (classified=${f.isClassified})")
    }
    
    // Check if secrets folder exists
    val secretsExists = access("secrets").exists
    println(s"\nsecrets folder exists: $secretsExists")
    
    // Check for docs folder
    if secretsExists then
        access("secrets").children.foreach { f =>
            println(s"  secrets/${f.name} (classified=${f.isClassified})")
        }
}
