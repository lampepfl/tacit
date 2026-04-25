requestFileSystem(".") {
    // Explore the src directory to find UsersController.scala
    val webapp = access("projects/webapp")
    val src = webapp.children.find(_.name == "src").get
    
    // Walk to find all Scala files
    println("Scala files in webapp:")
    src.walk().filter(_.name.endsWith(".scala")).foreach(f => println(s"  ${f.path}"))
}