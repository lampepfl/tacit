requestFileSystem(".") {
    // Look inside projects/ directory
    println("projects/ directory contents:")
    access("projects").children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
}