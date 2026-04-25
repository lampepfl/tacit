requestFileSystem(".") {
    // Check docs and src directories
    val webapp = access("projects/webapp")
    
    println("--- Docs directory ---")
    for f <- access("projects/webapp/docs").children do
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    
    println("\n--- Src directory structure ---")
    val srcDir = access("projects/webapp/src")
    // List top level
    for f <- srcDir.children do
        println(s"  [DIR] ${f.name}")
        // List children of src subdirectories
        if f.isDirectory then
            for g <- f.children do
                println(s"    ${if g.isDirectory then "[DIR]" else "[FILE]"} ${g.name}")
                // Go deeper if it's also a directory
                if g.isDirectory then
                    for h <- g.children do
                        println(s"      ${if h.isDirectory then "[DIR]" else "[FILE]"} ${h.name}")
}
