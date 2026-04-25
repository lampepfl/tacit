requestFileSystem(".") {
    val webapp = access("projects/webapp")
    // Let's see what's in docs
    val docs = access("projects/webapp/docs")
    docs.children.foreach { f =>
        println(s"  ${f.name}")
    }
}