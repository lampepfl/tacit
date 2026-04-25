requestFileSystem(".") {
    // Read the README.md in the root
    val readme = access("README.md")
    println("=== README.md Content ===")
    println(readme.read())
    
    // Also check for a README in the webapp directory
    val webappReadme = access("webapp/README.md")
    println("\n=== webapp/README.md Content ===")
    println(webappReadme.read())
}