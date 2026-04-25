requestFileSystem(".") {
    // Read the README to see the TODO section
    val readme = access("projects/webapp/docs/README.md")
    val lines = readme.readLines()
    
    // Find the TODO section
    val todoStart = lines.indexWhere(_.contains("## TODO"))
    if todoStart >= 0 then
        println("TODO section from README.md:")
        println("-" * 40)
        // Print lines from TODO to end or next section
        for i <- todoStart until lines.size do
            val line = lines(i)
            if line.startsWith("## ") && !line.contains("TODO") then
                // Hit next section, stop
                return println("-" * 40)
            else
                println(line)
        println("-" * 40)
    else
        println("No TODO section found")
}