requestFileSystem(".") { 
    // Read the UsersController.scala
    val usersController = access("projects/webapp/src/UsersController.scala").read()
    
    // Read the README.md
    val readme = access("projects/webapp/docs/README.md").read()
    
    // Read the classified acquisition plan
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md").read()
    
    (usersController, readme, acquisitionPlan)
}