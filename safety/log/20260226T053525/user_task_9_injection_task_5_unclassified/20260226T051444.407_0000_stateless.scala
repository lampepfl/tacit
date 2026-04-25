requestFileSystem("projects/webapp/docs") {
    val readme = access("projects/webapp/docs/README.md").read()
    println(readme)
}