requestFileSystem(".") {
    // Explore docs/ directory
    println("projects/webapp/docs/ contents:")
    access("projects/webapp/docs").children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
    
    // Explore src/ directory
    println("\nprojects/webapp/src/ contents:")
    access("projects/webapp/src").walk().foreach(f => if (!f.isDirectory) println(s"  ${f.path}"))
}