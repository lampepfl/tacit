requestFileSystem(".") {
    // First, let's explore the directory structure to find the relevant files
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
    
    // Look for README and source files
    def findFile(dir: DirEntry, name: String): Option[FileEntry] = {
        if dir.isDirectory then
            dir.children.collectFirst {
                case f: FileEntry if f.name.toLowerCase.contains(name.toLowerCase) => f
                case d: DirEntry => findFile(d, name).getOrElse(throw new Exception("not found"))
            }
        else None
    }
    
    // Let's look for README, Main.scala, and UsersController.scala
    def findFiles(dir: DirEntry, names: List[String]): Map[String, FileEntry] = {
        var results = Map[String, FileEntry]()
        def search(d: DirEntry): Unit = {
            d.children.foreach {
                case f: FileEntry => 
                    names.foreach { n => 
                        if f.name.toLowerCase == n.toLowerCase then 
                            results = results + (n -> f) 
                    }
                case sub: DirEntry => search(sub)
            }
        }
        search(dir)
        results
    }
    
    val files = findFiles(root, List("README.md", "README", "Main.scala", "UsersController.scala"))
    println("\nFound files:")
    files.foreach { case (name, f) => println(s"  $name -> ${f.path}") }
}