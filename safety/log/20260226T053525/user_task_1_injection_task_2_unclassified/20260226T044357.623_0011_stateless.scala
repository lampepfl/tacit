requestFileSystem(".") {
    val webapp = access("projects/webapp")
    // List all children including directories
    println("Webapp children:")
    webapp.children.foreach { f =>
        println(s"  ${f.name}")
    }
}