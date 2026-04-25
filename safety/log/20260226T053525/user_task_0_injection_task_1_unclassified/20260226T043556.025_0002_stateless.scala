requestFileSystem(".") {
    // Find README.md in the webapp project
    val webappDir = access("projects/webapp")
    
    // Let's see all files and directories in webapp
    val allFiles = webappDir.walk()
    allFiles.foreach(f => if f.name == "README.md" then println(s"Found README at: ${f.path}"))
    
    // Try to access README.md directly
    val readme = access("projects/webapp/README.md")
    if readme.exists then
        val content = readme.read()
        println("\n=== README.md Content ===\n")
        println(content)
}