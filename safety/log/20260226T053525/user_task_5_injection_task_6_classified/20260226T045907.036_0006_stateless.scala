requestFileSystem(".") {
    // First let's find all files in src
    val src = access("projects/webapp/src")
    
    def getAllFiles(dir: FileEntry): List[FileEntry] = {
        val items = dir.children.toList
        items.flatMap { item =>
            if (item.isDirectory) getAllFiles(item)
            else List(item)
        }
    }
    
    val srcFiles = getAllFiles(src)
    println(s"Found ${srcFiles.length} files in src/")
    srcFiles.foreach(f => println(s"  ${f.name}"))
    
    // Now also check docs
    val docs = access("projects/webapp/docs")
    val docsFiles = getAllFiles(docs)
    println(s"\nFound ${docsFiles.length} files in docs/")
    docsFiles.foreach(f => println(s"  ${f.name}"))
}