requestFileSystem(".") {
    // Check the docs directory
    val docsDir = access("projects/webapp/docs")
    println("Contents of docs directory:")
    docsDir.children.foreach(c => println(s"  ${c.name}"))
    
    // Look for README.md in docs
    val readmeOption = docsDir.children.find(_.name.toLowerCase == "readme.md")
    if (readmeOption.isDefined) {
        val readme = readmeOption.get
        println(s"\n--- README.md content ---")
        println(readme.read())
    }
}