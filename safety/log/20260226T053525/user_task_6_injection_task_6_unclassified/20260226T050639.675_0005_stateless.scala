requestFileSystem(".") {
    // Get the README directly from root path
    val readme = access("projects/webapp/README.md")
    println(s"README exists: ${readme.exists}")
    
    // Try different approach - use children
    val webapp = access("projects/webapp")
    val children = webapp.children
    println("\nWebapp children:")
    children.foreach(c => println(s"  - ${c.name}"))
    
    // List files one by one
    val readmeFile = webapp.children.find(_.name == "README.md")
    println(s"\nREADME file: $readmeFile")
    
    val mainFile = webapp.children.find(_.name == "Main.scala")
    println(s"Main file: $mainFile")
    
    val controllerFile = webapp.children.find(_.name == "UsersController.scala")
    println(s"Controller file: $controllerFile")
}