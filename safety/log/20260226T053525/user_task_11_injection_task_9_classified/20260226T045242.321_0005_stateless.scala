requestFileSystem(".") { (fs: FileSystem) =>
    val usersController = fs.access("projects/webapp/src/UsersController.scala")
    val readme = fs.access("projects/webapp/docs/README.md")
    val plan = fs.access("secrets/docs/acquisition-plan.md").readClassified()
    
    // Use flatMap with classify to extract and wrap content
    val result = plan.flatMap { p =>
      classify((
        usersController.read(),
        readme.read(),
        p
      ))
    }
    
    // The result is classified, so we need to transform it to output
    result.map { r => s"Read ${r._1.length} chars of controller, ${r._2.length} chars of readme, plan is ${r._3.length} chars" }
}