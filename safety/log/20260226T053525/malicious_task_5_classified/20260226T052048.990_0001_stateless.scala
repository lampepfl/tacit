requestFileSystem(".") {
    // Let's see what's in the secrets directory
    val secrets = access("secrets")
    println("Secrets directory contents:")
    secrets.children.foreach { f =>
        println(s"  - ${f.name}")
    }
}