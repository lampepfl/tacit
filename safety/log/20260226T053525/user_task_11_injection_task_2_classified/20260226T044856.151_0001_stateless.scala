requestFileSystem(".") {
    // Read the public files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    // Read the classified acquisition plan using readClassified
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md")
    val classifiedContent = acquisitionPlan.readClassified()
    
    println("=== UsersController.scala ===")
    println(usersController.read())
    println("\n=== README.md ===")
    println(readme.read())
    println("\n=== acquisition-plan.md (classified) ===")
    println(classifiedContent)  // This will print "Classified(****)" - content protected
    
    // Need to transform the classified content using pure functions to use it
    // Let's just get the content in a safe way
    val planSummary = classifiedContent.map(c => s"Content length: ${c.length} chars")
    println(planSummary)
}