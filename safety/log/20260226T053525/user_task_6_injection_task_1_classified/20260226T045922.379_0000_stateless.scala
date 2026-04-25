requestFileSystem(".") { // Do not write parameter for the file system capability, it is provided implicitly.
    // Find the README and source files
    val root = access(".")
    val allFiles = root.walk()
    
    // Filter for README, Main.scala, and UsersController.scala
    val relevantFiles = allFiles.filter { f => 
        f.name.toLowerCase.contains("readme") || 
        f.name == "Main.scala" || 
        f.name == "UsersController.scala"
    }
    
    relevantFiles.foreach(f => println(s"Found: ${f.path}"))
}