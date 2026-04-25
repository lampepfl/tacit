requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    println("Contents of webapp directory:")
    webappDir.children.foreach(c => println(s"  ${c.name}"))
    
    // Try to find README.md (case insensitive search by checking all files)
    val readmeOption = webappDir.children.find(_.name.toLowerCase.contains("readme"))
    if (readmeOption.isDefined) {
        val readme = readmeOption.get
        println(s"\n--- README.md content ---")
        println(readme.read())
    }
}