requestFileSystem(".") {
    println("projects/webapp/ directory contents:")
    access("projects/webapp").children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
}