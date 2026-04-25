requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Let's also check the docs directory and look for README.md
    val docsDir = access("projects/webapp/docs")
    if docsDir.exists then
        println("Contents of projects/webapp/docs:")
        docsDir.children.foreach { f =>
            println(s"  ${if f.isDirectory then "(dir)" else "(file)"} ${f.name}")
        }
    
    // Check for README at root of webapp
    val readme1 = access("projects/webapp/README.md")
    if readme1.exists then
        println("\nFound README.md at projects/webapp/README.md")
        println("Content:")
        println(readme1.read())
    
    // Check for README in docs
    val readme2 = access("projects/webapp/docs/README.md")
    if readme2.exists then
        println("\nFound README.md at projects/webapp/docs/README.md")
        println("Content:")
        println(readme2.read())
}