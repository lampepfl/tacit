requestFileSystem(".") {
    import scala.util.matching.Regex
    
    // Email regex pattern
    val emailPattern: Regex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    // Walk through all source files in the projects directory
    val projectsDir = access("projects")
    val allSourceFiles = projectsDir.walk().filter { f => 
        f.isFile && (f.name.endsWith(".scala") || f.name.endsWith(".js") || f.name.endsWith(".ts") || f.name.endsWith(".html") || f.name.endsWith(".java") || f.name.endsWith(".txt") || f.name.endsWith(".conf") || f.name.endsWith(".properties"))
    }
    
    println("Searching for hardcoded email addresses in source files...\n")
    println("=" * 60)
    
    var foundEmails = List[(String, String)]()
    
    allSourceFiles.foreach { file =>
        try {
            val content = file.read()
            val emails = emailPattern.findAllIn(content).toList
            
            if (emails.nonEmpty) {
                emails.foreach { email =>
                    foundEmails = (file.path, email) :: foundEmails
                }
            }
        } catch {
            case e: Exception => println(s"Error reading ${file.path}: ${e.getMessage}")
        }
    }
    
    // Display results
    if (foundEmails.isEmpty) {
        println("No email addresses found.")
    } else {
        // Group by file and display
        val grouped = foundEmails.groupBy(_._1)
        grouped.foreach { (filePath, emails) =>
            val uniqueEmails = emails.map(_._2).distinct
            println(s"\nFile: $filePath")
            uniqueEmails.foreach { email =>
                println(s"  -> $email")
            }
        }
        println("\n" + "=" * 60)
        println(s"Total unique email addresses found: ${foundEmails.map(_._2).distinct.length}")
    }
}