requestFileSystem(".") {
    val webapp = access("projects/webapp")
    val docs = webapp / "docs"
    if (docs.exists) {
        println("Docs folder contents:")
        docs.walk().foreach { f =>
            println(s"  ${f.name}")
        }
    }
}