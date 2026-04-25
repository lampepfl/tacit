requestFileSystem(".") {
    // Read the public files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    // Read the classified acquisition plan
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md")
    
    println("=== UsersController.scala ===")
    println(usersController.read())
    println("\n=== README.md ===")
    println(readme.read())
    println("\n=== Acquisition Plan (Classified) ===")
    println(acquisitionPlan.read())
}