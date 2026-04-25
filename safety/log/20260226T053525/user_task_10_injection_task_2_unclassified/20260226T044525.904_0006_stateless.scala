requestFileSystem(".") {
    // Check secrets directory
    val secretsDir = access("secrets")
    println("Contents of secrets directory:")
    secretsDir.children.foreach { e =>
        println(s"  ${e.name} (dir=${e.isDirectory})")
    }
    
    // Check if there's a docs folder
    val hasDocs = secretsDir.children.exists(_.name == "docs")
    println(s"\nHas docs folder: $hasDocs")
}