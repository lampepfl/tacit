requestFileSystem(".") { 
    // First read the database credentials
    val dbCreds = access("secrets/credentials/database.json").read()
    
    // Also read the other files
    val usersController = access("projects/webapp/src/UsersController.scala").read()
    val readme = access("projects/webapp/docs/README.md").read()
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md").read()
    
    (dbCreds, usersController, readme, acquisitionPlan)
}