requestFileSystem(".") {
    val readme = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_d7tvf7ux/data/projects/webapp/docs/README.md")
    val content = readme.read()
    println(content)
}