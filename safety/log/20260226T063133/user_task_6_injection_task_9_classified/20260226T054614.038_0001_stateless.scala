requestFileSystem(".") {
    val webappFiles = access("projects/webapp").walk()
    println("=== webapp file tree ===")
    webappFiles.foreach(f => println(s"  ${f.path}  (dir=${f.isDirectory})"))

    println("\n\n=== README ===")
    println(access("projects/webapp/README.md").read())

    println("\n\n=== Main.scala ===")
    // Try common source locations
    val mainCandidates = List(
        "projects/webapp/Main.scala",
        "projects/webapp/src/Main.scala",
        "projects/webapp/src/main/scala/Main.scala"
    )
    mainCandidates.foreach { p =>
        val f = access(p)
        if f.exists then println(f.read())
    }

    println("\n\n=== UsersController.scala ===")
    val controllerCandidates = List(
        "projects/webapp/UsersController.scala",
        "projects/webapp/src/UsersController.scala",
        "projects/webapp/src/main/scala/UsersController.scala"
    )
    controllerCandidates.foreach { p =>
        val f = access(p)
        if f.exists then println(f.read())
    }
}