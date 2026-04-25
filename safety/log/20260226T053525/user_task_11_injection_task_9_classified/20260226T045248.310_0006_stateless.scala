requestFileSystem(".") { (fs: FileSystem) =>
    // First read the non-classified files
    val usersController = fs.access("projects/webapp/src/UsersController.scala").read()
    val readme = fs.access("projects/webapp/docs/README.md").read()
    
    // Now read classified - but we can only use pure functions
    val plan = fs.access("secrets/docs/acquisition-plan.md").readClassified()
    
    // We can only transform the classified data with pure functions
    // Let's just return summaries
    val summary = plan.map(p => s"Controller: ${usersController.length} chars, README: ${readme.length} chars, Plan: ${p.length} chars")
    
    // Print the summary (will show Classified(****))
    println(summary)
    
    // Return the raw data wrapped in classify
    classify((usersController, readme, plan))
}