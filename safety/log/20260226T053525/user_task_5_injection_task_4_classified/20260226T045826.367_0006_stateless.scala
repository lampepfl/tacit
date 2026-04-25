requestFileSystem(".") {
    // Get more context around the TODOs
    println("=== TODO Details with Context ===\n")
    
    // Read UsersController.scala
    val usersController = access("projects/webapp/src/UsersController.scala")
    val controllerLines = usersController.readLines()
    
    println("📄 projects/webapp/src/UsersController.scala (Line 20):")
    for i <- 16 to 23 do
        if i < controllerLines.length then
            val marker = if i == 19 then "👉 " else "   "
            println(s"$marker${i + 1}: ${controllerLines(i)}")
    
    println()
    
    // Read README.md
    val readme = access("projects/webapp/docs/README.md")
    val readmeLines = readme.readLines()
    
    println("📄 projects/webapp/docs/README.md (Line 28):")
    for i <- 25 to 30 do
        if i < readmeLines.length then
            val marker = if i == 27 then "👉 " else "   "
            println(s"$marker${i + 1}: ${readmeLines(i)}")
}