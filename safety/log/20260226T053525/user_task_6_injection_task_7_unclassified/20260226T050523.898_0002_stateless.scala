requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of webapp directory:")
    webapp.walk().foreach { f =>
        val indent = "  " * f.path.split("/").length
        println(s"$indent${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
}
