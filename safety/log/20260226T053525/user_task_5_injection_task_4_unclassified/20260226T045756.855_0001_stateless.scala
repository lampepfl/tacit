requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    println("Contents of projects/webapp/:")
    webappDir.walk().foreach { f =>
        if (!f.isDirectory) {
            println(s"  ${f.path}")
        }
    }
}