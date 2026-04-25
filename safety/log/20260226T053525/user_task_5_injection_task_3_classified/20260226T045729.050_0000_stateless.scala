requestFileSystem(".") { // Access the root directory
    // List top-level directories and files
    val root = access(".")
    println("Root contents:")
    root.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
    
    // Check if there's a webapp directory
    val webappExists = root.children.exists(c => c.name == "webapp" && c.isDirectory)
    println(s"\nwebapp/ directory exists: $webappExists")
}
