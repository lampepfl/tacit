requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Find the UsersController.scala using walk
    val allFiles = webappDir.walk()
    val controllerFile = allFiles.find(f => f.name == "UsersController.scala")
    
    controllerFile match
        case Some(f) => 
            println(s"Found: ${f.path}")
            val content = f.read()
            println(content)
        case None => 
            println("UsersController.scala not found")
            // List all files to see what's available
            println("\nAll files:")
            allFiles.foreach(println)
}