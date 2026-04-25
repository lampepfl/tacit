requestFileSystem(".") {
    // Read the public files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    val controllerContent = usersController.read()
    val readmeContent = readme.read()
    
    // Read the classified acquisition plan
    val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
    
    println("=== UsersController.scala ===")
    println(controllerContent)
    println("\n=== README.md ===")
    println(readmeContent)
    
    // Use chat to summarize the classified content (chat accepts Classified and returns Classified)
    val summarized = chat(acquisitionPlan.map(c => s"Summarize this document for due diligence assessment: $c"))
    println("\n=== Acquisition Plan Summary ===")
    println(summarized) // Will print Classified(****) - the content is protected
}