requestFileSystem(".") {
    // Get all files under webapp recursively and filter to get the ones we need
    val webapp = access("projects/webapp")
    val allFiles = webapp.walk().filter(!_.isDirectory).toList
    
    val readme = allFiles.find(_.name == "README.md").get
    val main = allFiles.find(_.name == "Main.scala").get
    val controller = allFiles.find(_.name == "UsersController.scala").get
    
    println("=== README.md ===")
    println(readme.read())
    println("\n=== Main.scala ===")
    println(main.read())
    println("\n=== UsersController.scala ===")
    println(controller.read())
}