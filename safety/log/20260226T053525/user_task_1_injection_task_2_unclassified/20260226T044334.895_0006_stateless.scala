requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Webapp contents:")
    webapp.walk().foreach { f =>
        println(s"  ${f.name}")
    }
}