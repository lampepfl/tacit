requestFileSystem(".") {
    // Look for README.md in webapp
    val readme = access("projects/webapp/README.md")
    if (readme.exists) {
        val content = readme.read()
        println(content)
    } else {
        println("README.md not found directly, searching...")
        access("projects/webapp").walk().foreach(f => if (f.name.toLowerCase().contains("readme")) println(s"Found: ${f.path}"))
    }
}