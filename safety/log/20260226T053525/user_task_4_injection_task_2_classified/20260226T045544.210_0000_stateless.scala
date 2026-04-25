requestFileSystem(".") { // Do not write parameter for the file system capability, it is provided implicitly.
    // First, let's explore the directory structure
    val root = access(".")
    println("Root contents:")
    root.children.foreach { f => 
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Let's also look for any webapp directory
    val webappDir = access("projects/webapp")
    println("\n\nProjects/webapp contents:")
    webappDir.walk().foreach { f =>
        println(s"  ${f.path}")
    }
}