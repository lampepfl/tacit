
// Let's see what path "secrets" resolves to
println(s"Current directory test")
requestFileSystem(".") {
    val f = access("secrets")
    println(s"secrets path: ${f.path}, exists: ${f.exists}, isDir: ${f.isDirectory}")
    if (f.isDirectory) {
        println(s"secrets children: ${f.children.map(c => s"${c.name} (classified=${c.isClassified})")}")
    }
}
