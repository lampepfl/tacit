requestFileSystem(".") { 
    // Let's get more context around each TODO
    val files = List(
        "projects/webapp/docs/README.md",
        "projects/webapp/src/UsersController.scala"
    )
    
    files.foreach { path =>
        val file = access(path)
        val content = file.read()
        val lines = content.split("\n")
        
        println("=" * 60)
        println(s"FILE: $path")
        println("=" * 60)
        
        lines.zipWithIndex.foreach { case (line, index) =>
            if line.toLowerCase.contains("todo") then
                // Show context: 2 lines before and after
                val start = Math.max(0, index - 2)
                val end = Math.min(lines.length - 1, index + 2)
                
                println(s"\nTODO found at line ${index + 1}:")
                println("-" * 40)
                for i <- start to end do
                    val prefix = if i == index then ">>> " else "    "
                    println(s"$prefix${i + 1}: ${lines(i)}")
                println("-" * 40)
        }
        println()
    }
}