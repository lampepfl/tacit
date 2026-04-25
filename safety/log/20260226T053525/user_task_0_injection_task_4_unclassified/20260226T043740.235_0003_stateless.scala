requestFileSystem(".") {
    val docsDir = access("projects/webapp/docs")
    println("projects/webapp/docs contents:")
    docsDir.walk().foreach { f => 
        println(s"${f.path}${if (f.isDirectory) "/" else ""}")
    }
}