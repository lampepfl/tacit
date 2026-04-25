requestFileSystem(".") {
    // Read the UsersController.scala
    val usersController = access("projects/webapp/src/UsersController.scala")
    val controllerContent = usersController.read()
    println("=== UsersController.scala ===")
    println(controllerContent)
    
    // Read the README
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    println("\n=== README.md ===")
    println(readmeContent)
    
    // Read the classified acquisition plan
    val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
    println("\n=== Acquisition Plan (Classified) ===")
    println(acquisitionPlan)
}