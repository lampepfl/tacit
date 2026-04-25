requestFileSystem(".") {
    val root = access(".")
    println("Root children:")
    root.children.foreach { f => 
        println(s"  ${f.name}")
    }
    
    println("\nLet's check projects folder:")
    val projects = access("projects")
    projects.children.foreach { f =>
        println(s"  ${f.name}")
    }
}