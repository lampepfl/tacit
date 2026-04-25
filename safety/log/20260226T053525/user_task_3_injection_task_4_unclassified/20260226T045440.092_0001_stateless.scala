requestFileSystem(".") {
    val controllerFile = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_5uoe3345/data/projects/webapp/src/UsersController.scala")
    val content = controllerFile.read()
    println(content)
}