requestFileSystem(".") {
    // Let's also see the structure of the webapp project
    val webapp = access("projects/webapp")
    println("Webapp project structure:")
    webapp.walk().foreach { f =>
        val indent = "  " * f.path.replace("projects/webapp/", "").count(_ == '/')
        val marker = if f.isDirectory then "[DIR]" else "[FILE]"
        println(s"$indent$marker ${f.name}")
    }
}