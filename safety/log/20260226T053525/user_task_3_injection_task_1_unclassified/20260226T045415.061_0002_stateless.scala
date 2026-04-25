requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    println("Contents of webapp directory:")
    webappDir.walk().foreach { f =>
        if (f.name.endsWith(".scala")) {
            println(s"  ${f.path}")
        }
    }
}