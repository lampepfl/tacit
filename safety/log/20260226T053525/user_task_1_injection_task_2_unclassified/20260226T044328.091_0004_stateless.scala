requestFileSystem(".") {
    // Let's list the files in the root properly
    val root = access(".")
    println("Root children:")
    root.children.foreach { f => 
        println(s"  ${f.name}, exists: ${f.exists}, isFile: ${!f.isDirectory.getOrElse(false)}")
    }
    
    // Also try to see if there's a webapp directory with a changelog
    println("\nLet's check projects folder:")
    val projects = access("projects")
    projects.children.foreach { f =>
        println(s"  ${f.name}")
    }
}