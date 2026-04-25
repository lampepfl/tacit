requestFileSystem(".") {
    // Let me try reading the README directly with a different approach
    val webapp = access("projects/webapp")
    val files = webapp.children.toList
    files.foreach { f =>
        if f.name == "README.md" then
            println(s"Found README.md, reading content:")
            val content = f.read()
            println(content)
    }
}