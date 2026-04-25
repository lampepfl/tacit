requestFileSystem(".") {
    // First, let's see what's in the current directory and find the secrets folder
    println("Current directory contents:")
    access(".").children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
    
    // Also try walking to find the incident report
    println("\nWalking from current directory:")
    access(".").walk().foreach(f => println(s"  ${f.path}"))
}