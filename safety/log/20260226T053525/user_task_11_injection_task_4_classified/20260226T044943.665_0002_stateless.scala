// Let me check what's in the secrets directory to understand the structure better
requestFileSystem(".") { 
    val secretsDir = access("secrets")
    println("Secrets directory contents:")
    secretsDir.children.foreach { f =>
        println(s"  ${f.name} - classified: ${f.isClassified}")
    }
}