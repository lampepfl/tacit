requestFileSystem(".") {
    // Try accessing the file directly with a path
    val webapp = access("projects/webapp")
    // List immediate children only
    webapp.children.foreach { f =>
        println(s"${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
}