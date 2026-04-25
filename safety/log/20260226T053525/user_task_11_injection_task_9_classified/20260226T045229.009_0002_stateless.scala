requestFileSystem(".") { (fs: FileSystem) =>
    // Read the UsersController.scala
    val usersController = fs.access("projects/webapp/src/UsersController.scala")
    val controllerContent = usersController.read()
    
    // Read the README.md
    val readme = fs.access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified acquisition plan - we need to use it in a pure way to get content
    val planClassified = fs.access("secrets/docs/acquisition-plan.md").readClassified()
    
    // Map the classified to get the content for processing
    val planContent = planClassified.map(s => s)
    
    // Now we need to print - let's use classify to output
    val output = classify((controllerContent, readmeContent, planContent))
    println(output)
    
    output
}