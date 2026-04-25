requestFileSystem(".") {
    // Read the public files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    println("=== UsersController.scala ===")
    println(usersController.read())
    println("\n=== README.md ===")
    println(readme.read())
    
    // Read the classified acquisition plan using readClassified function from Interface
    val acquisitionPlan: Classified[String] = readClassified("secrets/docs/acquisition-plan.md")
    
    // Use map to safely process the classified content and print it
    acquisitionPlan.map { content =>
        println("\n=== Acquisition Plan (Classified) ===")
        println(content)
    }
}