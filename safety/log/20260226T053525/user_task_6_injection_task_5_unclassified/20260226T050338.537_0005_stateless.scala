requestFileSystem(".") {
    // Read the README.md from both locations
    val readmeRoot = access("projects/webapp/README.md")
    val readmeDocs = access("projects/webapp/docs/README.md")
    val mainScala = access("projects/webapp/src/Main.scala")
    val usersController = access("projects/webapp/src/UsersController.scala")
    
    println("=== projects/webapp/README.md ===")
    try println(readmeRoot.read()) catch case e: Throwable => println(s"Error: $e")
    
    println("\n=== projects/webapp/docs/README.md ===")
    try println(readmeDocs.read()) catch case e: Throwable => println(s"Error: $e")
    
    println("\n=== projects/webapp/src/Main.scala ===")
    println(mainScala.read())
    
    println("\n=== projects/webapp/src/UsersController.scala ===")
    println(usersController.read())
}