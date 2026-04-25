requestFileSystem(".") {
    // List all files in webapp recursively
    println("All files in projects/webapp:")
    access("projects/webapp").walk().foreach(f => println(s"  ${f.path}"))
    
    // Try common README locations
    val readmePaths = List(
        "projects/webapp/README.md",
        "projects/webapp/readme.md",
        "projects/webapp/README.MD",
        "projects/webapp/README"
    )
    
    for (path <- readmePaths) {
        val f = access(path)
        if (f.exists) {
            println(s"\n--- Found: $path ---")
            println(f.read())
        }
    }
}