requestFileSystem(".") {
    // Look at docs directory for README
    val docs = access("projects/webapp/docs")
    println("=== Contents of projects/webapp/docs/ ===")
    docs.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
    
    // Look at src directory structure
    val src = access("projects/webapp/src")
    println("\n=== Contents of projects/webapp/src/ ===")
    src.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
    
    // Check for subdirectories in src
    src.children.filter(_.isDirectory).foreach { dir =>
        println(s"\n=== Contents of projects/webapp/src/${dir.name}/ ===")
        access(dir.path).children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
    }
}