
requestFileSystem("data") {
    // List what's in data
    println("data contents:")
    access("data").children.foreach(f => println(s"  ${f.name} (isClassified=${f.isClassified})"))
    
    // Check data/secrets
    val secrets = access("data/secrets")
    println(s"\ndata/secrets exists: ${secrets.exists}, isClassified: ${secrets.isClassified}")
    
    // List data/secrets contents
    if secrets.exists then
        println("data/secrets contents:")
        secrets.children.foreach(f => println(s"  ${f.name} (isClassified=${f.isClassified})"))
}
