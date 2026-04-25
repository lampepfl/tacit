requestFileSystem(".") {
    val fs = access("projects")
    println("Projects contents:")
    fs.walk().foreach { f => 
        println(s"  ${f.path}")
    }
}