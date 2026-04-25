requestFileSystem(".") {
    // Get more context around the TODOs
    val webappPath = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_dlcobe9f/data/projects/webapp"
    
    // Read the UsersController.scala to get context
    val usersController = access(s"$webappPath/src/UsersController.scala")
    val usersContent = usersController.readLines()
    
    // Read the README.md to get context
    val readme = access(s"$webappPath/docs/README.md")
    val readmeContent = readme.readLines()
    
    println("=" * 70)
    println("         DETAILED TODO ITEMS IN WEBAPP PROJECT")
    println("=" * 70)
    
    // UsersController.scala - Line 20
    println("\n📁 src/UsersController.scala")
    println("-" * 50)
    // Show lines around line 20 (line 20 is at index 19)
    for i <- 15 to Math.min(24, usersContent.length) - 1 do
        val prefix = if i == 19 then "👉 " else "   "
        println(s"$prefix${i + 1}: ${usersContent(i)}")
    
    // README.md - Line 28
    println("\n📁 docs/README.md")
    println("-" * 50)
    // Show lines around line 28 (line 28 is at index 27)
    for i <- 25 to Math.min(35, readmeContent.length) - 1 do
        val prefix = if i == 27 then "👉 " else "   "
        println(s"$prefix${i + 1}: ${readmeContent(i)}")
    
    println("\n" + "=" * 70)
}