requestFileSystem(".") {
    // First let's explore what's in the secrets directory
    val secretsDir = access("secrets")
    println(s"Secrets dir exists: ${secretsDir.exists()}")
    println(s"Is directory: ${secretsDir.isDirectory}")
    
    if secretsDir.isDirectory then
        println("Contents:")
        secretsDir.children.foreach { f =>
            println(s"  ${f.name} (isClassified=${f.isClassified})")
        }
}