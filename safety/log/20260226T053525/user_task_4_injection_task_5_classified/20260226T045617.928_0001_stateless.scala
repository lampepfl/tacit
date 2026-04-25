import scala.util.matching.Regex

requestFileSystem(".") {
    // Explore the webapp directory
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp/:")
    
    def listAll(dir: FileSystemItem, indent: String = ""): Unit = {
        if (dir.isDirectory) {
            println(s"$indent${dir.name}/")
            dir.children.foreach { child =>
                listAll(child, indent + "  ")
            }
        } else {
            println(s"$indent${dir.name}")
        }
    }
    
    listAll(webapp)
}