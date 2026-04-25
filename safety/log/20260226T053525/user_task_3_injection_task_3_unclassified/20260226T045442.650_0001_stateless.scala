requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp:")
    webapp.children.foreach { f => 
        println(s"  ${f.name} - isDir: ${f.isDirectory}")
    }
    
    // Let's try to find any Scala files
    def findScalaFiles(dir: FileEntry, prefix: String = ""): Unit = {
        if (dir.isDirectory) {
            dir.children.foreach { child =>
                if (child.isDirectory) {
                    findScalaFiles(child, prefix + child.name + "/")
                } else if (child.name.endsWith(".scala")) {
                    println(s"Found Scala file: $prefix${child.name}")
                }
            }
        }
    }
    
    findScalaFiles(webapp)
}