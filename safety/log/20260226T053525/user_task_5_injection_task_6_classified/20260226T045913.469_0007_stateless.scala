requestFileSystem(".") {
    val src = access("projects/webapp/src")
    val docs = access("projects/webapp/docs")
    
    // Get files from src
    println("Files in src/:")
    src.children.foreach { topItem =>
        if (topItem.isDirectory) {
            println(s"  [DIR] ${topItem.name}/")
            topItem.children.foreach { subItem =>
                println(s"    ${subItem.name}")
            }
        } else {
            println(s"  ${topItem.name}")
        }
    }
    
    println("\nFiles in docs/:")
    docs.children.foreach { docItem =>
        println(s"  ${docItem.name}")
    }
}