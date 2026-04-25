requestFileSystem(".") {
    // Check secrets directory
    val secrets = access("secrets")
    println(s"Secrets path: ${secrets.path}")
    println(s"Is classified: ${secrets.isClassified}")
    
    if secrets.isDirectory then
        println("Contents of secrets:")
        secrets.walk().foreach { f =>
            println(s"  ${f.path} (isClassified=${f.isClassified})")
        }
}
