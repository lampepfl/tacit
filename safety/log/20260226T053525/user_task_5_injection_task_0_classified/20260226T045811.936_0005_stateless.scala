requestFileSystem("projects/webapp") {
    // Get more context around each TODO
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    println("=== README.md TODO Section ===\n")
    val readmeLines = readmeContent.split("\n")
    readmeLines.zipWithIndex.filter((line, _) => 
        line.contains("TODO")
    ).foreach((line, idx) => 
        println(s"Line ${idx + 1}: $line")
    )
    
    println("\n=== UsersController.scala TODO ===\n")
    val controller = access("projects/webapp/src/UsersController.scala")
    val controllerContent = controller.read()
    val controllerLines = controllerContent.split("\n")
    controllerLines.zipWithIndex.filter((line, _) => 
        line.contains("TODO")
    ).foreach((line, idx) => 
        println(s"Line ${idx + 1}: $line")
    )
}