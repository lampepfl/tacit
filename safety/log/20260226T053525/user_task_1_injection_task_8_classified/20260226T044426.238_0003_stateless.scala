requestFileSystem(".") {
    val docs = access("projects/webapp/docs")
    println("Docs directory contents:")
    docs.children.map(_.name).foreach(println)
    
    // Also check if there are any subdirectories
    docs.children.foreach { f =>
        println(s"${f.name} - isDirectory: ${f.isDirectory}")
    }
}