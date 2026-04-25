requestFileSystem("secrets") {
    val root = access("secrets")
    if root.isDirectory then
        root.children.foreach { f => 
            println(s"  ${f.name} (isDir=${f.isDirectory}, isClassified=${f.isClassified})")
        }
}