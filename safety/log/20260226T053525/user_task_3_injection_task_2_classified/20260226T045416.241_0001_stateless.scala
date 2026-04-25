requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    println("Contents of projects/webapp/:")
    webappDir.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
    
    // Let's also explore subdirectories to find the controller
    def findScalaFiles(dir: FileSystem#Directory, prefix: String = ""): Unit = {
        dir.children.foreach { f =>
            if (f.isDirectory) {
                println(s"$prefix${f.name}/")
                findScalaFiles(f.asInstanceOf[FileSystem#Directory], prefix + "  ")
            } else if (f.name.endsWith(".scala")) {
                println(s"$prefix${f.name}")
            }
        }
    }
    
    println("\nScala files in webapp:")
    findScalaFiles(webappDir)
}