requestFileSystem("projects") {
    // Let's see what's in the webapp directory
    val webappDir = access("projects/webapp")
    println("Contents of webapp directory:")
    webappDir.walk().foreach { f =>
        println(s"  ${f.path}")
    }
}