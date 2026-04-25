requestFileSystem(".") {
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    println("=== TODO Details with Context ===\n")
    
    println("1. UsersController.scala (Line 20):")
    println("-" * 40)
    val controllerContent = usersController.read()
    val controllerLines = controllerContent.split("\n")
    // Show lines around line 20 (0-indexed as 19)
    for i <- 17 to 22 do
        val prefix = if i == 19 then ">>> " else "    "
        println(s"$prefix${i+1}: ${controllerLines(i)}")
    
    println("\n\n2. README.md (Line 42):")
    println("-" * 40)
    val readmeContent = readme.read()
    val readmeLines = readmeContent.split("\n")
    // Show lines around line 42 (0-indexed as 41)
    for i <- 40 to 45 do
        if i < readmeLines.length then
            val prefix = if i == 41 then ">>> " else "    "
            println(s"$prefix${i+1}: ${readmeLines(i)}")
}