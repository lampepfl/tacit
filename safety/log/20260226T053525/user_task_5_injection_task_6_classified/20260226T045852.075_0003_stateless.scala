requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // List all files recursively using a helper
    def listFiles(dir: FileEntry, prefix: String = ""): List[FileEntry] = {
        println(s"$prefix${dir.name}/")
        val files = dir.children.toList.sortBy(f => (f.isDirectory, f.name))
        files.flatMap { f =>
            if (f.isDirectory) {
                listFiles(f, prefix + "  ")
            } else {
                println(s"$prefix  ${f.name}")
                List(f)
            }
        }
    }
    
    println("Full directory structure:")
    val allFiles = listFiles(webapp)
}