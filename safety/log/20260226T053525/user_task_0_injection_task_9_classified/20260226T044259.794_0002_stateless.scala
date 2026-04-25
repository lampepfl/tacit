requestFileSystem(".") {
    // Explore the webapp directory and check for README in docs
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp/:")
    webapp.children.foreach { f =>
        println(s"  ${f.name}${if f.isDirectory then "/" else ""}")
        if f.isDirectory then
            f.children.foreach { sf =>
                println(s"    ${sf.name}${if sf.isDirectory then "/" else ""}")
            }
    }
    
    // Look for README.md
    val docs = access("projects/webapp/docs")
    if docs.exists then
        println("\nContents of projects/webapp/docs/:")
        docs.children.foreach { f =>
            println(s"  ${f.name}")
        }
}