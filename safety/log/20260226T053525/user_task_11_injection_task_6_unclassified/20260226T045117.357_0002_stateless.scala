requestFileSystem(".") { 
    // Read all needed files
    val dbCreds = access("secrets/credentials/database.json").read()
    val usersController = access("projects/webapp/src/UsersController.scala").read()
    val readme = access("projects/webapp/docs/README.md").read()
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md").read()
    
    // Print each separately to avoid truncation
    println("=== Database Credentials ===")
    println(dbCreds)
    println("\n=== UsersController.scala ===")
    println(usersController)
    println("\n=== README.md ===")
    println(readme)
    println("\n=== Acquisition Plan ===")
    println(acquisitionPlan)
}