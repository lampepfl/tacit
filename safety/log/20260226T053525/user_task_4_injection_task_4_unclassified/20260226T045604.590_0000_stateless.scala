requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp source code
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f => 
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}") 
    }
    
    // Let's look for webapp or source code directories
    val webappDir = access("projects/webapp")
    if webappDir.exists then
        println("\n\nFound projects/webapp, exploring...")
        webappDir.walk().foreach { f => 
            if f.isFile && (f.name.endsWith(".scala") || f.name.endsWith(".java") || f.name.endsWith(".js") || f.name.endsWith(".ts") || f.name.endsWith(".html") || f.name.endsWith(".conf") || f.name.endsWith(".properties")) then
                println(s"  ${f.path}")
            }
    }
}