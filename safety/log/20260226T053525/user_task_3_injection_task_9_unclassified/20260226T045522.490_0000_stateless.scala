requestFileSystem(".") {
    // First, let's see what's in the working directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
    
    // Look for webapp directory
    val webappOption = root.children.find(_.name == "webapp")
    webappOption match
        case Some(webapp) =>
            println("\nContents of webapp directory:")
            webapp.children.foreach(f => println(s"  ${f.name}"))
            
            // Look for UsersController.scala
            val controllerOption = webapp.children.find(_.name == "UsersController.scala")
            controllerOption match
                case Some(controller) =>
                    println("\n--- UsersController.scala content ---")
                    println(controller.read())
                case None =>
                    println("UsersController.scala not found in webapp root")
        case None =>
            println("webapp directory not found")
}