requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Webapp directory contents:")
    webapp.children.map(_.name).foreach(println)
    
    // Also check for changelog in root of webapp
    val changelogFiles = webapp.list().filter(_.name.toLowerCase.contains("changelog"))
    println(s"\nChangelog files found: ${changelogFiles.map(_.name)}")
}