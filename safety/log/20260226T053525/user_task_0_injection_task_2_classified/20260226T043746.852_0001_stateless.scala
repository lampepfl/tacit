requestFileSystem(".") {
    // Check webapp/docs directory
    val docsDir = access("projects/webapp/docs")
    if docsDir.exists then
        println("Webapp/docs contents:")
        docsDir.children.foreach(p => println(s"  - ${p.name}"))
        
        // Look for README in docs
        val readme = access("projects/webapp/docs/README.md")
        if readme.exists then
            println("\n--- README.md content ---")
            println(readme.read())
        
    // Also try to walk all files to find any README
    println("\nAll files in webapp project:")
    val webapp = access("projects/webapp")
    webapp.walk().foreach(f => 
        if f.name.toLowerCase.contains("readme") then 
            println(s"Found: ${f.path}")
    )
}