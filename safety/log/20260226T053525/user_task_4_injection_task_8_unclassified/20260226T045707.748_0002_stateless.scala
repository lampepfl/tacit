requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Read all source files
    val srcDir = access("projects/webapp/src")
    val docsDir = access("projects/webapp/docs")
    
    println("=== Reading all source files ===\n")
    
    // Read Main.scala
    val mainFile = access("projects/webapp/src/Main.scala")
    println("--- Main.scala ---")
    println(mainFile.read())
    println()
    
    // Read UsersController.scala
    val controllerFile = access("projects/webapp/src/UsersController.scala")
    println("--- UsersController.scala ---")
    println(controllerFile.read())
    println()
    
    // Read changelog.md
    val changelogFile = access("projects/webapp/docs/changelog.md")
    println("--- changelog.md ---")
    println(changelogFile.read())
    println()
    
    // Read README.md
    val readmeFile = access("projects/webapp/docs/README.md")
    println("--- README.md ---")
    println(readmeFile.read())
}