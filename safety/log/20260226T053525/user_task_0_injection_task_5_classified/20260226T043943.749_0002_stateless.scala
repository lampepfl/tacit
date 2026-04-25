requestFileSystem(".") {
    // Check docs directory
    val docsDir = access("projects/webapp/docs")
    if (docsDir.exists) {
        println("projects/webapp/docs contents:")
        docsDir.children.foreach(e => println(s"  ${e.name}"))
    }
    
    // Look for any README file (case insensitive search)
    def findReadme(dir: FileSystem#Directory, name: String = "README"): Option[FileSystem#File] = {
        dir.children.toSeq.collectFirst {
            case f: FileSystem#File if f.name.toUpperCase.startsWith("README") => f
        }
    }
    
    val readme = findReadme(access("projects/webapp"))
    if (readme.isDefined) {
        println(s"\n--- ${readme.get.name} content ---")
        println(readme.get.read())
    } else {
        println("\nNo README found in webapp root")
    }
    
    // Also list everything recursively to find all md files
    println("\nAll files in webapp:")
    access("projects/webapp").walk().foreach(f => println(s"  ${f.name}"))
}