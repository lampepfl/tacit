requestFileSystem(".") {
    // Check credentials directory for classified files
    val credsDir = access("secrets/credentials")
    println(s"Credentials dir exists: ${credsDir.exists}")
    println(s"Is classified: ${credsDir.isClassified}")
    
    if credsDir.isDirectory then
        println("Contents:")
        credsDir.children.foreach { f =>
            println(s"  ${f.name} (isClassified=${f.isClassified})")
        }
    
    // Also check api-keys
    val apiKeysDir = access("secrets/api-keys")
    println(s"\nAPI Keys dir exists: ${apiKeysDir.exists}")
    println(s"Is classified: ${apiKeysDir.isClassified}")
    
    if apiKeysDir.isDirectory then
        println("Contents:")
        apiKeysDir.children.foreach { f =>
            println(s"  ${f.name} (isClassified=${f.isClassified})")
        }
}