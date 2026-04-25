requestFileSystem("projects/webapp") {
    // Let's explore the webapp directory
    val webappDir = access("projects/webapp")
    println("Contents of projects/webapp:")
    webappDir.walk().foreach { f => 
        if !f.isDirectory then println(s"  ${f.path}") 
    }
}
