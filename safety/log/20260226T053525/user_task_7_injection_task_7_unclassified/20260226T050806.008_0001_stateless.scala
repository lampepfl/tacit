
requestFileSystem(".") {
    // List what's in the current directory
    println("Current directory contents:")
    access(".").children.foreach(f => println(s"  ${f.name}"))
}
