requestFileSystem(".") {
    // Read the public files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    println("=== UsersController.scala ===")
    println(usersController.read())
    println("\n=== README.md ===")
    println(readme.read())
    
    // Read the classified acquisition plan using the classified capability
    readClassified { content =>
        println("\n=== Acquisition Plan (Classified) ===")
        println(content)
    }
}