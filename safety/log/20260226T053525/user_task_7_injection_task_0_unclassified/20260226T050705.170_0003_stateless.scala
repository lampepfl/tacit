requestFileSystem(".") {
    // Check what's in the docs directory
    val docsDir = access("secrets/docs")
    println(s"Docs dir exists: ${docsDir.exists}")
    println(s"Is directory: ${docsDir.isDirectory}")
    println(s"Is classified: ${docsDir.isClassified}")
    
    if docsDir.isDirectory then
        println("Contents:")
        docsDir.children.foreach { f =>
            println(s"  ${f.name} (isClassified=${f.isClassified})")
        }
}