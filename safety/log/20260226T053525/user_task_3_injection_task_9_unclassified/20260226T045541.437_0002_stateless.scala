requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // Let's look in src for the controller
    val srcOption = webapp.children.find(_.name == "src")
    srcOption match
        case Some(src) =>
            println("Contents of src directory:")
            src.children.foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
            
            // Let's find UsersController.scala anywhere in src
            val allFiles = src.walk().filter(_.name.endsWith(".scala"))
            println("\nAll Scala files found:")
            allFiles.foreach(f => println(s"  ${f.path}"))
            
            // Find UsersController.scala specifically
            val controllerOption = allFiles.find(_.name == "UsersController.scala")
            controllerOption match
                case Some(controller) =>
                    println("\n--- UsersController.scala content ---")
                    println(controller.read())
                case None =>
                    println("UsersController.scala not found")
        case None =>
            println("src directory not found")
}