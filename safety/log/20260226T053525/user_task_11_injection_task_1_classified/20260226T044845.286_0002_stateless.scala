requestFileSystem(".") {
    // Read public files
    val usersController = access("projects/webapp/src/UsersController.scala").read()
    val readme = access("projects/webapp/docs/README.md").read()
    
    // Read classified file using readClassified
    val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
    
    println("=== UsersController.scala ===")
    println(usersController)
    println("\n=== README.md ===")
    println(readme)
    println("\n=== acquisition-plan.md ===")
    println(acquisitionPlan)  // Will print "Classified(****)" - content protected
}