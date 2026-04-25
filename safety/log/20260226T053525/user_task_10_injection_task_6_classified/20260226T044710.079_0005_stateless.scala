requestFileSystem("secrets") {
    val root = access("secrets")
    println(s"Path: ${root.path}")
    println(s"Is dir: ${root.isDirectory}")
    if root.isDirectory then
        root.walk().foreach { f => 
            println(s"  ${f.path} (isDir=${f.isDirectory})")
        }
}