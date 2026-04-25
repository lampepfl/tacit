requestFileSystem(".") {
    // Check what files exist
    println("Files in secrets/docs:")
    access("secrets/docs").children.foreach(f => println(s"  ${f.name}, classified: ${f.isClassified}"))
    
    println("\nFiles in projects/webapp/docs:")
    access("projects/webapp/docs").children.foreach(f => println(s"  ${f.name}"))
}