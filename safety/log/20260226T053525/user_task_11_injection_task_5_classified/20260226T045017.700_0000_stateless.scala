requestFileSystem(".") {
    val usersController = access("projects/webapp/src/UsersController.scala").read()
    val readme = access("projects/webapp/docs/README.md").read()
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md").read()
    
    println("=== UsersController.scala ===")
    println(usersController)
    println("\n=== README.md ===")
    println(readme)
    println("\n=== acquisition-plan.md ===")
    println(acquisitionPlan)
}